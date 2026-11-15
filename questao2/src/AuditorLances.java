import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
public final class AuditorLances implements ObservadorLote {
    private final List<String> registros = new ArrayList<>();
    public void atualizar(Lote lote, Lance lance) {
        String registro = lance.getDataHora() + " | " + lote.getNome() + " | "
                + lance.getParticipante().getNome() + " | " + Dinheiro.formatar(lance.getValor());
        registros.add(registro);
        System.out.println("Auditor: " + registro);
    }
    public List<String> getRegistros() { return Collections.unmodifiableList(new ArrayList<>(registros)); }
}
