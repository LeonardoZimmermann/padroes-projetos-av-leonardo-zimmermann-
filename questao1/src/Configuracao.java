import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public final class Configuracao {
    private final String regiaoPadrao;
    private final BigDecimal orcamentoMensalMaximo;
    private Configuracao() {
        Properties propriedades = new Properties();
        String caminho = System.getProperty("config.path", "config.properties");
        try (Reader reader = Files.newBufferedReader(Paths.get(caminho), StandardCharsets.UTF_8)) {
            propriedades.load(reader);
        } catch (IOException e) {
            throw new IllegalStateException("Nao foi possivel ler " + caminho, e);
        }
        regiaoPadrao = propriedades.getProperty("regiao.padrao", "").trim();
        if (regiaoPadrao.isEmpty()) throw new IllegalArgumentException("Regiao padrao obrigatoria");
        try {
            orcamentoMensalMaximo = new BigDecimal(propriedades.getProperty("orcamento.mensal.maximo", "").trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Orcamento mensal invalido", e);
        }
        if (orcamentoMensalMaximo.signum() < 0)
            throw new IllegalArgumentException("Orcamento mensal nao pode ser negativo");
    }
    private static class Holder {
        private static final Configuracao INSTANCIA = new Configuracao();
    }
    public static Configuracao getInstancia() { return Holder.INSTANCIA; }
    public String getRegiaoPadrao() { return regiaoPadrao; }
    public BigDecimal getOrcamentoMensalMaximo() { return orcamentoMensalMaximo; }
}
