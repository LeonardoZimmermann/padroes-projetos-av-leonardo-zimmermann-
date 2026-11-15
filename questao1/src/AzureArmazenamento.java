import java.math.BigDecimal;
public final class AzureArmazenamento extends RecursoBase implements Armazenamento {
    public AzureArmazenamento() { super("blob://", "Azure"); }
    public BigDecimal getCustoPorGB() { return AzureFactory.CUSTO_GB; }
}
