import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
public final class PainelLeiloeiro implements ObservadorLote {
    private final Map<String, String> resumos = new LinkedHashMap<>();
    public void atualizar(Lote lote, Lance lance) {
        String resumo = "Painel: " + lote.getNome() + " | maior lance=" + Dinheiro.formatar(lote.getLanceAtual())
                + " | total de lances=" + lote.getLances().size();
        resumos.put(lote.getNome(), resumo);
        System.out.println(resumo);
    }
    public Map<String, String> getResumos() { return Collections.unmodifiableMap(new LinkedHashMap<>(resumos)); }
}
