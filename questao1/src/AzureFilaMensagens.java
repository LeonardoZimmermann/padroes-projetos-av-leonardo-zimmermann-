import java.math.BigDecimal;
public final class AzureFilaMensagens extends RecursoBase implements FilaMensagens {
    public AzureFilaMensagens() { super("sb-", "Azure"); }
    public BigDecimal getCustoMensal() { return AzureFactory.CUSTO_FILA; }
}
