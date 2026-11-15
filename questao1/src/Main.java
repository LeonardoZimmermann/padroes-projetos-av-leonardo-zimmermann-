import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
public final class Main {
    public static void main(String[] args) throws Exception {
        Configuracao.getInstancia();
        Provisionador provisionador = new Provisionador();
        CountDownLatch inicio = new CountDownLatch(1);
        List<Callable<Resultado>> tarefas = new ArrayList<>();
        tarefas.add(() -> {
            inicio.await();
            return provisionador.provisionar(new Solicitacao("AWS-desenvolvimento", "AWS", 1, 100), new AWSFactory());
        });
        tarefas.add(() -> {
            inicio.await();
            return provisionador.provisionar(new Solicitacao("AWS-producao", "AWS", 2, 200), new AWSFactory());
        });
        tarefas.add(() -> provisionador.provisionar(new Solicitacao("Azure-homologacao", "Azure", 1, 100), new AzureFactory()));
        tarefas.add(() -> provisionador.provisionar(new Solicitacao("AWS-acima-orcamento", "AWS", 10, 1000), new AWSFactory()));
        ExecutorService executor = Executors.newFixedThreadPool(2);
        try {
            List<Future<Resultado>> futuros = new ArrayList<>();
            for (Callable<Resultado> tarefa : tarefas) futuros.add(executor.submit(tarefa));
            inicio.countDown();
            int aceitos = 0;
            BigDecimal total = BigDecimal.ZERO;
            for (Future<Resultado> futuro : futuros) {
                Resultado resultado = futuro.get();
                if (resultado.isAceito()) {
                    aceitos++;
                    total = total.add(resultado.getCusto());
                } else System.out.println(resultado);
            }
            System.out.println("=== RESUMO: aceitos=" + aceitos + " recusados=" + (futuros.size() - aceitos)
                    + " custo mensal total=R$ " + total.toPlainString() + " ===");
        } finally {
            inicio.countDown();
            executor.shutdown();
            System.out.println("=== AUDITORIA COMPLETA ===");
            for (String registro : Auditoria.getInstancia().getRegistros()) System.out.println(registro);
        }
    }
}
