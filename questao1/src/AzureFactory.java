import java.math.BigDecimal;
public final class AzureFactory implements ProvedorFactory {
    static final BigDecimal CUSTO_HORA_VM = new BigDecimal("0.12");
    static final BigDecimal CUSTO_GB = new BigDecimal("0.02");
    static final BigDecimal CUSTO_FILA = new BigDecimal("0.50");
    public String getNome() { return "Azure"; }
    public BigDecimal getCustoHoraMaquina() { return CUSTO_HORA_VM; }
    public BigDecimal getCustoPorGB() { return CUSTO_GB; }
    public BigDecimal getCustoMensalFila() { return CUSTO_FILA; }
    public MaquinaVirtual criarMaquinaVirtual() { return new AzureMaquinaVirtual(); }
    public Armazenamento criarArmazenamento() { return new AzureArmazenamento(); }
    public FilaMensagens criarFilaMensagens() { return new AzureFilaMensagens(); }
}
