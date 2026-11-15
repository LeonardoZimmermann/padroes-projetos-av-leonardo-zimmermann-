import java.math.BigDecimal;
public final class AWSMaquinaVirtual extends RecursoBase implements MaquinaVirtual {
    public AWSMaquinaVirtual() { super("i-", "AWS"); }
    public BigDecimal getCustoHora() { return AWSFactory.CUSTO_HORA_VM; }
}
