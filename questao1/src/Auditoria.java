import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
public final class Auditoria {
    private final List<String> registros = new ArrayList<>();
    private Auditoria() { }
    private static class Holder {
        private static final Auditoria INSTANCIA = new Auditoria();
    }
    public static Auditoria getInstancia() { return Holder.INSTANCIA; }
    public synchronized void registrar(Solicitacao solicitacao, boolean aceito, String motivo) {
        registros.add(String.format("%04d | %s | %s | ambiente=%s | provedor=%s | %s | %s",
                registros.size() + 1, Instant.now(), Thread.currentThread().getName(),
                solicitacao.getNome(), solicitacao.getProvedor(), aceito ? "ACEITO" : "RECUSADO", motivo));
    }
    public synchronized List<String> getRegistros() {
        return Collections.unmodifiableList(new ArrayList<>(registros));
    }
}
