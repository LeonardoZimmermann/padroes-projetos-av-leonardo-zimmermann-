import java.math.BigDecimal;
public final class AWSFilaMensagens extends RecursoBase implements FilaMensagens {
    public AWSFilaMensagens() { super("sqs-", "AWS"); }
    public BigDecimal getCustoMensal() { return AWSFactory.CUSTO_FILA; }
}
