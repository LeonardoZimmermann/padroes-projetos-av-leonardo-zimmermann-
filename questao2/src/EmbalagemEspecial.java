import java.math.BigDecimal;
public final class EmbalagemEspecial extends ServicoAdicional {
    public EmbalagemEspecial(Fatura fatura) { super(fatura); }
    protected ItemFatura criarItem() { return new ItemFatura("Embalagem especial", new BigDecimal("180.00")); }
}
