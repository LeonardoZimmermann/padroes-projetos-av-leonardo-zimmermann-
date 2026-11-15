import java.math.BigDecimal;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;

public final class Testes {
    private static void verificar(boolean condicao, String mensagem) {
        if (!condicao) throw new AssertionError(mensagem);
    }
    private static long numero(Recurso recurso, String prefixo) {
        verificar(recurso.getIdentificador().startsWith(prefixo), "Prefixo incorreto");
        return Long.parseLong(recurso.getIdentificador().substring(prefixo.length()));
    }
    public static void main(String[] args) throws Exception {
        ExecutorService pool = Executors.newFixedThreadPool(8);
        PrintStream saida = System.out;
        try {
            CountDownLatch inicio = new CountDownLatch(1);
            List<Future<Configuracao>> instancias = new ArrayList<>();
            List<Future<Auditoria>> auditorias = new ArrayList<>();
            for (int i = 0; i < 32; i++) {
                instancias.add(pool.submit(() -> { inicio.await(); return Configuracao.getInstancia(); }));
                auditorias.add(pool.submit(() -> { inicio.await(); return Auditoria.getInstancia(); }));
            }
            inicio.countDown();
            Configuracao config = instancias.get(0).get();
            Auditoria log = auditorias.get(0).get();
            for (Future<Configuracao> f : instancias) verificar(f.get() == config, "Singleton config");
            for (Future<Auditoria> f : auditorias) verificar(f.get() == log, "Singleton auditoria");
            verificar(config.getRegiaoPadrao().equals("brazil-south"), "Regiao do arquivo");
            verificar(config.getOrcamentoMensalMaximo().compareTo(new BigDecimal("200")) == 0, "Orcamento do arquivo");
            String caminhoAnterior = System.getProperty("config.path");
            try {
                System.setProperty("config.path", "arquivo-que-nao-existe.properties");
                verificar(Configuracao.getInstancia() == config, "Nao deve reler arquivo");
                verificar(config.getOrcamentoMensalMaximo().compareTo(new BigDecimal("200")) == 0, "Valor imutavel");
            } finally {
                if (caminhoAnterior == null) System.clearProperty("config.path");
                else System.setProperty("config.path", caminhoAnterior);
            }
            Provisionador p = new Provisionador();
            ProvedorFactory[] fabricas = {new AWSFactory(), new AzureFactory()};
            String[][] prefixos = {{"i-", "s3://", "sqs-"}, {"vm-", "blob://", "sb-"}};
            String[] custos = {"75.70", "90.10"};
            // Evita centenas de resumos no teste de carga.
            System.setOut(new PrintStream(new ByteArrayOutputStream()));
            for (int f = 0; f < fabricas.length; f++) {
                ProvedorFactory fabrica = fabricas[f];
                Solicitacao s = new Solicitacao("basico", fabrica.getNome(), 1, 100);
                verificar(p.estimarCusto(s, fabrica).compareTo(new BigDecimal(custos[f])) == 0, "730 horas e tarifas");
                Resultado aceito = p.provisionar(s, fabrica);
                verificar(aceito.isAceito() && aceito.getRecursos().size() == 3, "Ambiente completo");
                verificar(aceito.getRegiao().equals(config.getRegiaoPadrao()), "Regiao no resumo");
                verificar(aceito.toString().contains("basico") && aceito.toString().contains(custos[f]), "Resumo");
                long[] anteriores = new long[3];
                for (int i = 0; i < 3; i++) {
                    Recurso recurso = aceito.getRecursos().get(i);
                    anteriores[i] = numero(recurso, prefixos[f][i]);
                    verificar(recurso.getProvedor().equals(fabrica.getNome()), "Familia consistente");
                    verificar(anteriores[i] == 1, "Sequencia inicia em um por tipo e provedor");
                }
                verificar(((MaquinaVirtual) aceito.getRecursos().get(0)).getCustoHora().equals(fabrica.getCustoHoraMaquina()), "Preco VM");
                verificar(((Armazenamento) aceito.getRecursos().get(1)).getCustoPorGB().equals(fabrica.getCustoPorGB()), "Preco armazenamento");
                verificar(((FilaMensagens) aceito.getRecursos().get(2)).getCustoMensal().equals(fabrica.getCustoMensalFila()), "Preco fila");
                Resultado negado = p.provisionar(new Solicitacao("caro", fabrica.getNome(), 10, 1000), fabrica);
                verificar(!negado.isAceito() && negado.getRecursos().isEmpty(), "Recusa sem recursos");
                Resultado seguinte = p.provisionar(s, fabrica);
                for (int i = 0; i < 3; i++) verificar(numero(seguinte.getRecursos().get(i), prefixos[f][i]) == anteriores[i] + 1, "Recusa nao consome IDs");
            }
            Resultado limite = p.provisionar(new Solicitacao("limite-exato", "Azure", 2, 1215), new AzureFactory());
            verificar(limite.isAceito() && limite.getCusto().compareTo(new BigDecimal("200")) == 0, "Limite exato aceito");
            Resultado divergente = p.provisionar(new Solicitacao("divergente", "AWS", 1, 10), new AzureFactory());
            verificar(!divergente.isAceito() && divergente.getRecursos().isEmpty(), "Fabrica divergente recusada");
            List<Recurso> misturados = new ArrayList<>();
            misturados.add(new AWSFactory().criarMaquinaVirtual());
            misturados.add(new AzureFactory().criarArmazenamento());
            try {
                new Resultado(new Solicitacao("misturado", "AWS", 1, 1), "regiao", BigDecimal.ONE, true, "teste", misturados);
                throw new AssertionError("Ambiente misturado aceito");
            } catch (IllegalArgumentException esperado) { }
            try {
                new Solicitacao("invalida", "AWS", -1, 1);
                throw new AssertionError("Quantidade invalida aceita");
            } catch (IllegalArgumentException esperado) { }
            List<String> snapshot = log.getRegistros();
            int antes = snapshot.size();
            Resultado referencia = p.provisionar(new Solicitacao("referencia", "AWS", 2, 100), new AWSFactory());
            long vmInicial = numero(referencia.getRecursos().get(1), "i-");
            long storageInicial = numero(referencia.getRecursos().get(2), "s3://");
            long filaInicial = numero(referencia.getRecursos().get(3), "sqs-");
            List<Future<Resultado>> futuros = new ArrayList<>();
            for (int i = 0; i < 200; i++) {
                final int indice = i;
                futuros.add(pool.submit(() -> p.provisionar(new Solicitacao("paralela-" + indice, "AWS", 2, 100), new AWSFactory())));
            }
            Set<Long> vms = new HashSet<>(), storages = new HashSet<>(), filas = new HashSet<>();
            for (Future<Resultado> futuro : futuros) {
                Resultado resultado = futuro.get();
                verificar(resultado.isAceito() && resultado.getRecursos().size() == 4, "Duas VMs, storage e fila");
                verificar(vms.add(numero(resultado.getRecursos().get(0), "i-")), "VM duplicada");
                verificar(vms.add(numero(resultado.getRecursos().get(1), "i-")), "VM duplicada");
                verificar(storages.add(numero(resultado.getRecursos().get(2), "s3://")), "Storage duplicado");
                verificar(filas.add(numero(resultado.getRecursos().get(3), "sqs-")), "Fila duplicada");
            }
            for (long i = 1; i <= 400; i++) verificar(vms.contains(vmInicial + i), "Sequencia VM");
            for (long i = 1; i <= 200; i++) {
                verificar(storages.contains(storageInicial + i), "Sequencia storage");
                verificar(filas.contains(filaInicial + i), "Sequencia fila");
            }
            verificar(log.getRegistros().size() == antes + 201, "Um registro por solicitacao, sem perdas");
            verificar(snapshot.size() == antes, "Snapshot independente");
            List<String> registros = log.getRegistros();
            for (int i = 0; i < registros.size(); i++) {
                String registro = registros.get(i);
                verificar(registro.startsWith(String.format("%04d |", i + 1)), "Ordem da auditoria");
                verificar(registro.contains("ambiente=") && registro.contains("provedor=") &&
                        (registro.contains("ACEITO") || registro.contains("RECUSADO")), "Campos auditoria");
            }
            try { snapshot.add("alteracao"); throw new AssertionError("Historico mutavel"); }
            catch (UnsupportedOperationException esperado) { }
            saida.println("OK: tarifas, prefixos, 730 horas, configuracao unica, orcamento, familias, 800 IDs concorrentes e auditoria sem perdas.");
        } finally {
            System.setOut(saida);
            pool.shutdownNow();
        }
    }
}
