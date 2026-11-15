import java.math.BigDecimal;
public interface ProvedorFactory {
    String getNome();
    BigDecimal getCustoHoraMaquina();
    BigDecimal getCustoPorGB();
    BigDecimal getCustoMensalFila();
    MaquinaVirtual criarMaquinaVirtual();
    Armazenamento criarArmazenamento();
    FilaMensagens criarFilaMensagens();
}
