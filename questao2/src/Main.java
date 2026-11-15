import java.math.BigDecimal;
public final class Main {
    public static void main(String[] args) {
        Lote arte = new Lote("Quadro Horizonte", "Arte", new BigDecimal("1000.00"),
                new BigDecimal("1200.00"), new ComissaoArte());
        Lote veiculo = new Lote("Carro classico", "Veiculos", new BigDecimal("10000.00"),
                new BigDecimal("15000.00"), new ComissaoVeiculos());
        Participante ana = new Participante("Ana");
        Participante bruno = new Participante("Bruno");
        Participante carla = new Participante("Carla");
        PainelLeiloeiro painel = new PainelLeiloeiro();
        AuditorLances auditor = new AuditorLances();
        for (Lote lote : new Lote[] {arte, veiculo}) {
            lote.acompanhar(ana);
            lote.acompanhar(bruno);
            lote.acompanhar(carla);
            lote.acompanhar(painel);
            lote.acompanhar(auditor);
        }
        ana.darLance(arte, new BigDecimal("1050.00"));
        bruno.darLance(arte, new BigDecimal("1080.00")); // Recusado: aumento inferior a 5%.
        bruno.darLance(arte, new BigDecimal("1150.00"));
        arte.deixarDeAcompanhar(carla);
        System.out.println("Carla deixou de acompanhar o quadro");
        ana.darLance(arte, new BigDecimal("1250.00"));
        arte.trocarRegraComissao(new ComissaoEletronicos());
        System.out.println("Campanha: quadro usa agora a comissao de Eletronicos");
        carla.darLance(veiculo, new BigDecimal("10500.00"));
        bruno.darLance(veiculo, new BigDecimal("10600.00")); // Recusado.
        Venda venda = arte.encerrar().orElseThrow(() -> new IllegalStateException("Venda esperada"));
        veiculo.encerrar(); // A reserva de 15 mil nao foi atingida.
        bruno.darLance(arte, new BigDecimal("1500.00")); // Recusado: lote encerrado.
        Fatura fatura = new FaturaVenda(venda);
        fatura = new SeguroTransporte(fatura);
        fatura = new CertificadoAutenticidade(fatura);
        fatura = new EmbalagemEspecial(fatura);
        fatura = new EmbalagemEspecial(fatura); // Mesmo servico contratado duas vezes.
        fatura = new EntregaExpressa(fatura);
        new ImpressorFatura().imprimir(fatura);
        new AcertoVendedor(venda).imprimir();
        System.out.println("=== HISTORICO DE LANCES ACEITOS ===");
        for (String registro : auditor.getRegistros()) System.out.println(registro);
    }
}
