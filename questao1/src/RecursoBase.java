import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

public abstract class RecursoBase implements Recurso {
    // Cada prefixo identifica um tipo de recurso de um provedor.
    private static final ConcurrentMap<String, AtomicLong> SEQUENCIAS = new ConcurrentHashMap<>();
    private final String identificador;
    private final String provedor;
    protected RecursoBase(String prefixo, String provedor) {
        long numero = SEQUENCIAS.computeIfAbsent(prefixo, chave -> new AtomicLong())
                .updateAndGet(valor -> Math.incrementExact(valor));
        this.identificador = prefixo + numero;
        this.provedor = provedor;
    }
    public final String getIdentificador() { return identificador; }
    public final String getProvedor() { return provedor; }
}
