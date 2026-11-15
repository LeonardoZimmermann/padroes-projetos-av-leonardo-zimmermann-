import java.math.BigDecimal;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

public final class Testes {
    private static void verificar(boolean condicao, String mensagem) {
        if (!condicao) throw new AssertionError(mensagem);
    }
    private static void valor(BigDecimal atual, String esperado, String mensagem) {
        verificar(atual.compareTo(new BigDecimal(esperado)) == 0, mensagem + ": " + atual);
    }
    private static void falha(Runnable acao, String mensagem) {
        try { acao.run(); }
        catch (IllegalArgumentException | IllegalStateException | ArithmeticException esperado) { return; }
        throw new AssertionError(mensagem);
    }
    private static Lote lote(String nome, String inicial, String reserva, RegraComissao regra) {
        return new Lote(nome, "Teste", new BigDecimal(inicial), new BigDecimal(reserva), regra);
    }
    public static void main(String[] args) {
        PrintStream saida = System.out;
        ByteArrayOutputStream mensagens = new ByteArrayOutputStream();
        System.setOut(new PrintStream(mensagens));
        try {
            Participante ana = new Participante("Ana");
            Participante bruno = new Participante("Bruno");
            Participante carla = new Participante("Carla");
            PainelLeiloeiro painel = new PainelLeiloeiro();
            AuditorLances auditor = new AuditorLances();
            Lote arte = lote("Quadro", "1000", "1200", new ComissaoArte());
            for (ObservadorLote interessado : new ObservadorLote[] {ana, bruno, carla, painel, auditor})
                arte.acompanhar(interessado);
            arte.acompanhar(bruno); // Nao pode duplicar notificacoes.
            valor(arte.getLanceAtual(), "1000", "Lance inicial");
            verificar(!ana.darLance(arte, new BigDecimal("1049.99")), "Abaixo de 5% recusado");
            verificar(arte.getLances().isEmpty() && auditor.getRegistros().isEmpty(), "Recusa nao altera historico");
            verificar(ana.darLance(arte, new BigDecimal("1050")), "Exatamente 5% aceito");
            verificar(ana.getNotificacoes().isEmpty(), "Autor nao recebe seu lance");
            verificar(bruno.getNotificacoes().size() == 1 && carla.getNotificacoes().size() == 1, "Aviso sem duplicacao");
            verificar(bruno.getNotificacoes().get(0).contains("Quadro") && bruno.getNotificacoes().get(0).contains("1050,00"), "Nome e valor no aviso");
            verificar(!bruno.darLance(arte, new BigDecimal("1080")), "Segundo lance insuficiente");
            verificar(bruno.darLance(arte, new BigDecimal("1150")), "Segundo lance aceito");
            verificar(bruno.getNotificacoes().size() == 1, "Segundo autor excluido");
            arte.deixarDeAcompanhar(carla);
            verificar(ana.darLance(arte, new BigDecimal("1250")), "Terceiro lance aceito");
            verificar(carla.getNotificacoes().size() == 2, "Saida do acompanhamento");
            verificar(painel.getResumos().get("Quadro").contains("total de lances=3"), "Painel conta somente aceitos");
            verificar(painel.getResumos().get("Quadro").contains("1250,00"), "Painel maior lance");
            verificar(auditor.getRegistros().size() == 3, "Auditoria de todos aceitos");
            Lance primeiro = arte.getLances().get(0);
            verificar(primeiro.getParticipante() == ana && primeiro.getDataHora() != null, "Autor e data/hora");
            verificar(auditor.getRegistros().get(0).contains(primeiro.getDataHora().toString()), "Data/hora na auditoria");
            List<Lance> snapshot = arte.getLances();
            try { snapshot.clear(); throw new AssertionError("Lances mutaveis"); }
            catch (UnsupportedOperationException esperado) { }

            // As quatro Strategies, incluindo teto, minimo e arredondamento monetario.
            valor(new ComissaoArte().calcular(new BigDecimal("1000")), "200", "Arte 20%");
            valor(new ComissaoVeiculos().calcular(new BigDecimal("10000")), "800", "Veiculos 8%");
            valor(new ComissaoVeiculos().calcular(new BigDecimal("100000")), "5000", "Teto veiculos");
            valor(new ComissaoVeiculos().calcular(new BigDecimal("62500")), "5000", "Limite do teto");
            valor(new ComissaoImoveis().calcular(new BigDecimal("100000")), "4500", "Imoveis 3% mais fixo");
            valor(new ComissaoEletronicos().calcular(new BigDecimal("100")), "50", "Minimo eletronicos");
            valor(new ComissaoEletronicos().calcular(new BigDecimal("1000")), "120", "Eletronicos 12%");
            valor(new ComissaoArte().calcular(new BigDecimal("100.03")), "20.01", "Comissao arredondada");
            arte.trocarRegraComissao(new ComissaoEletronicos());
            Venda venda = arte.encerrar().orElseThrow(() -> new AssertionError("Deveria vender"));
            verificar(arte.getEstado() == EstadoLote.VENDIDO && venda.getComprador() == ana, "Estado e vencedor");
            valor(venda.getValor(), "1250", "Valor da venda");
            valor(venda.getComissao(), "150", "Campanha aplicada no encerramento");
            verificar(!bruno.darLance(arte, new BigDecimal("2000")), "Encerrado nao aceita lance");
            verificar(auditor.getRegistros().size() == 3, "Recusa apos encerramento nao notifica");
            falha(() -> arte.trocarRegraComissao(new ComissaoArte()), "Troca apos encerramento");
            falha(() -> arte.encerrar(), "Duplo encerramento");
            Lote carro = lote("Carro", "10000", "15000", new ComissaoVeiculos());
            carro.acompanhar(painel);
            carro.acompanhar(auditor);
            verificar(carla.darLance(carro, new BigDecimal("10500")), "Lance no segundo lote");
            verificar(!carro.encerrar().isPresent() && carro.getEstado() == EstadoLote.NAO_VENDIDO, "Reserva nao atingida");
            verificar(painel.getResumos().size() == 2 && auditor.getRegistros().size() == 4, "Interessados em dois lotes");
            Lote semLances = lote("Sem lances", "100", "50", new ComissaoArte());
            verificar(!semLances.encerrar().isPresent(), "Sem comprador nao vende, mesmo acima da reserva");
            Lote reservaExata = lote("Reserva exata", "100", "105", new ComissaoArte());
            ana.darLance(reservaExata, new BigDecimal("105"));
            verificar(reservaExata.encerrar().isPresent(), "Reserva exata vende");
            Lote centavos = lote("Centavos", "0.10", "0.10", new ComissaoArte());
            verificar(!ana.darLance(centavos, new BigDecimal("0.10")), "5% nao pode arredondar para baixo");
            verificar(ana.darLance(centavos, new BigDecimal("0.11")), "Menor centavo valido");
            falha(() -> lote("Invalido", "-1", "10", new ComissaoArte()), "Preco negativo");
            falha(() -> Dinheiro.validar(new BigDecimal("1.001")), "Fracoes de centavo na entrada");

            Fatura base = new FaturaVenda(venda);
            ImpressorFatura impressor = new ImpressorFatura();
            verificar(base.getItens().isEmpty(), "Venda sem servicos");
            valor(base.getTotal(), "1250", "Total sem servicos");
            verificar(impressor.gerar(base).contains("Total a pagar: R$ 1250,00"), "Mesmo impressor sem servicos");
            Fatura completa = new EntregaExpressa(new EmbalagemEspecial(new EmbalagemEspecial(
                    new CertificadoAutenticidade(new SeguroTransporte(base)))));
            verificar(completa.getItens().size() == 5, "Cinco servicos, incluindo repeticao");
            valor(completa.getItens().get(0).getCusto(), "18.75", "Seguro sobre valor de venda");
            valor(completa.getItens().get(1).getCusto(), "300", "Certificado");
            valor(completa.getItens().get(2).getCusto(), "180", "Embalagem");
            valor(completa.getItens().get(3).getCusto(), "180", "Embalagem repetida");
            valor(completa.getItens().get(4).getCusto(), "450", "Entrega");
            valor(completa.getTotal(), "2378.75", "Total com servicos");
            verificar(completa.getVenda() == venda && base.getItens().isEmpty(), "Decoracao preserva venda e base");
            Fatura outraOrdem = new SeguroTransporte(new EntregaExpressa(new CertificadoAutenticidade(
                    new EmbalagemEspecial(new EmbalagemEspecial(base)))));
            valor(outraOrdem.getTotal(), "2378.75", "Ordem nao altera custos");
            verificar(outraOrdem.getItens().get(0).getDescricao().equals("Embalagem especial"), "Ordem dos itens preservada");
            Fatura doisSeguros = new SeguroTransporte(new SeguroTransporte(base));
            valor(doisSeguros.getTotal(), "1287.50", "Seguro repetido usa valor de venda");
            String fatura = impressor.gerar(completa);
            verificar(fatura.contains("Quadro") && fatura.contains("Seguro de transporte") && fatura.contains("2378,75"), "Fatura detalhada");
            verificar(fatura.indexOf("Embalagem especial") != fatura.lastIndexOf("Embalagem especial"), "Repeticao impressa");
            AcertoVendedor acerto = new AcertoVendedor(venda);
            valor(acerto.getLiquido(), "1100", "Liquido independe dos adicionais do comprador");
            verificar(acerto.gerar().contains("Comissao: R$ 150,00"), "Comissao no acerto");
            verificar(mensagens.toString().contains("minimo R$ 1050,00") && mensagens.toString().contains("lote encerrado"), "Motivos de recusa");
            saida.println("OK: Observer, lances/reservas, quatro Strategies, campanhas, Decorator/repeticoes, fatura e acerto.");
        } finally { System.setOut(saida); }
    }
}
