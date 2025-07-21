import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

class leitor {
    public static void main(String[] args) {
        ArrayList<Integer[]> labirinto = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader("labirinto.dat"))) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                Integer[] vals = new Integer[linha.length()];
                for (int i = 0; i < linha.length(); i++) {
                    vals[i] = Integer.parseInt(String.valueOf(linha.charAt(i)));
                }
                labirinto.add(vals);
            }
        } catch (IOException e) {
            System.out.println("Erro ao ler o arquivo: " + e.getMessage());
            return;
        }

        ArrayList<ArrayList<Vertice>> caminhos = AlgoritmoAEstrela.menorCaminho(labirinto);

        if (caminhos.isEmpty()) {
            System.out.println("Nenhum caminho encontrado.");
        } else {
            int i = 1;
            for (ArrayList<Vertice> caminho : caminhos) {
                System.out.println("Caminho " + i++ + ":");
                for (int j = caminho.size() - 1; j >= 0; j--) {
                    System.out.println(caminho.get(j));
                }
                System.out.println("--------");
            }
        }
    }
}

