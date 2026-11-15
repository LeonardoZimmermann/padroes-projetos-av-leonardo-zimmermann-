import java.math.BigDecimal;
public final class AWSFactory implements ProvedorFactory {
    static final BigDecimal CUSTO_HORA_VM = new BigDecimal("0.10");
    static final BigDecimal CUSTO_GB = new BigDecimal("0.023");
    static final BigDecimal CUSTO_FILA = new BigDecimal("0.40");
    public String getNome() { return "AWS"; }
    public BigDecimal getCustoHoraMaquina() { return CUSTO_HORA_VM; }
    public BigDecimal getCustoPorGB() { return CUSTO_GB; }
    public BigDecimal getCustoMensalFila() { return CUSTO_FILA; }
    public MaquinaVirtual criarMaquinaVirtual() { return new AWSMaquinaVirtual(); }
    public Armazenamento criarArmazenamento() { return new AWSArmazenamento(); }
    public FilaMensagens criarFilaMensagens() { return new AWSFilaMensagens(); }
}
