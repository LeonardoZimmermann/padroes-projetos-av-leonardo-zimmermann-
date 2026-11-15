import java.math.BigDecimal;
public final class ComissaoArte implements RegraComissao {
    public BigDecimal calcular(BigDecimal valorVenda) {
        BigDecimal valor = Dinheiro.validar(valorVenda);
        return Dinheiro.arredondar(valor.multiply(new BigDecimal("0.20")));
    }
}
