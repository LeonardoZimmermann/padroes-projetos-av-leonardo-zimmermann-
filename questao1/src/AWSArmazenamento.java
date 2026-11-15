import java.math.BigDecimal;
public final class AWSArmazenamento extends RecursoBase implements Armazenamento {
    public AWSArmazenamento() { super("s3://", "AWS"); }
    public BigDecimal getCustoPorGB() { return AWSFactory.CUSTO_GB; }
}
