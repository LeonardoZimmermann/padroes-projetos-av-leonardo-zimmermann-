import java.math.BigDecimal;
public final class AzureMaquinaVirtual extends RecursoBase implements MaquinaVirtual {
    public AzureMaquinaVirtual() { super("vm-", "Azure"); }
    public BigDecimal getCustoHora() { return AzureFactory.CUSTO_HORA_VM; }
}
