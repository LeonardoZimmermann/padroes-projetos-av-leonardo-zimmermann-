import java.math.BigDecimal;
public final class SeguroTransporte extends ServicoAdicional {
    public SeguroTransporte(Fatura fatura) { super(fatura); }
    protected ItemFatura criarItem() { return new ItemFatura("Seguro de transporte", Dinheiro.arredondar(getVenda().getValor().multiply(new BigDecimal("0.015")))); }
}
