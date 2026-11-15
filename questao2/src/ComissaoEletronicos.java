import java.math.BigDecimal;
public final class ComissaoEletronicos implements RegraComissao {
    public BigDecimal calcular(BigDecimal valorVenda) {
        BigDecimal valor = Dinheiro.validar(valorVenda);
        return Dinheiro.arredondar(valor.multiply(new BigDecimal("0.12")).max(new BigDecimal("50.00")));
    }
}
