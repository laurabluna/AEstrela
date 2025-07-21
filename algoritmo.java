import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

class Algoritmo {
    public static void main(String[] args) {
        ArrayList<Integer[]> labirinto = new ArrayList<Integer[]>();
        try (BufferedReader reader = new BufferedReader(new FileReader("labirinto.dat"))) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                Integer[] vals = new Integer[linha.length()];
                for (int i = 0; i < linha.length(); i++)
                    vals[i] = Integer.parseInt(linha.charAt(i) + "");
                labirinto.add(vals);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        ArrayList<ArrayList<Vertice>> caminhos = AlgoritmoAEstrela.menorCaminho(labirinto);

        for (ArrayList<Vertice> caminho : caminhos) {
            for (Vertice vertice : caminho) {
                System.out.println(vertice);
            }
            System.out.println();
        }
    }
}

class AlgoritmoAEstrela {

    public static ArrayList<ArrayList<Vertice>> menorCaminho(ArrayList<Integer[]> labirinto) {
        Vertice entrada = null;
        ArrayList<ArrayList<Vertice>> caminhos = new ArrayList<ArrayList<Vertice>>();
        ArrayList<ArrayList<Vertice>> matrizDeVertices = new ArrayList<ArrayList<Vertice>>();
        ArrayList<Vertice> saidas = new ArrayList<Vertice>();
        for (int i = 0; i < labirinto.size(); i++) {
            ArrayList<Vertice> linhaDeVertices = new ArrayList<Vertice>();
            for (int j = 0; j < labirinto.get(i).length; j++) {
                if (labirinto.get(i)[j] == 2) {
                    entrada = new Vertice(i, j);
                    entrada.setValor(2);
                    linhaDeVertices.add(entrada);
                } else if (labirinto.get(i)[j] == 3) {
                    Vertice saida = new Vertice(i, j);
                    saidas.add(saida);
                    saida.setValor(3);
                    linhaDeVertices.add(saida);
                } else {
                    Vertice vertice = new Vertice(i, j);
                    vertice.setValor(labirinto.get(i)[j]);
                    linhaDeVertices.add(vertice);
                }
            }
            matrizDeVertices.add(linhaDeVertices);
        }
        for (int j = 0; j < matrizDeVertices.size(); j++) {
            for (int i = 0; i < matrizDeVertices.get(j).size(); i++) {
                matrizDeVertices.get(j).get(i).setAdjacentes(matrizDeVertices);
            }
        }
        for (Vertice saida : saidas) {
            ArrayList<Vertice> abertos = new ArrayList<Vertice>();
            ArrayList<Vertice> fechados = new ArrayList<Vertice>();
            abertos.add(entrada);
            entrada.setG(0);
            entrada.setH(heuristic(entrada, saida));
            caminhos.add(visitarAdjacentes(entrada, saida, abertos, fechados));
        }
        return caminhos;

    }

    public static Integer heuristic(Vertice entrada, Vertice saida) {
        return (int) Math.sqrt(Math.pow(entrada.getX() - saida.getX(), 2) + Math.pow(entrada.getY() - saida.getY(), 2));
    }

    public static ArrayList<Vertice> visitarAdjacentes(Vertice atual, Vertice saida, ArrayList<Vertice> abertos,
            ArrayList<Vertice> fechados) {
        for (Vertice adjacente : atual.getAdjacentes()) {
            if (adjacente == saida) {
                ArrayList<Vertice> caminho = new ArrayList<Vertice>();
                caminho.add(adjacente);
                Vertice vertice = atual;
                while (vertice != null) {
                    caminho.add(vertice);
                    vertice = vertice.getAntecessor();
                }
                return caminho;
            }
            if (fechados.contains(adjacente))
                continue;
            if (!abertos.contains(adjacente)) {
                abertos.add(adjacente);
                if (atual.getX() == adjacente.getX() || atual.getY() == adjacente.getY())
                    adjacente.setG(atual.getG() + 10);
                else
                    adjacente.setG(atual.getG() + 14);
                adjacente.setH(heuristic(adjacente, saida));
                adjacente.setAntecessor(atual);
            } else {
                if (atual.getX() == adjacente.getX() || atual.getY() == adjacente.getY()) {
                    if (atual.getG() + 10 < adjacente.getG()) {
                        adjacente.setG(atual.getG() + 10);
                        adjacente.setAntecessor(atual);
                    }
                } else {
                    if (atual.getG() + 14 < adjacente.getG()) {
                        adjacente.setG(atual.getG() + 14);
                        adjacente.setAntecessor(atual);
                    }
                }
            }
        }
        fechados.add(atual);
        abertos.remove(atual);
        if (abertos.size() == 0)
            return new ArrayList<Vertice>();
        abertos.sort((v1, v2) -> v1.getF() - v2.getF());
        return visitarAdjacentes(abertos.get(0), saida, abertos, fechados);

    }
}

class Vertice {
    private ArrayList<Vertice> adjacentes = new ArrayList<Vertice>(8);
    private Vertice antecessor;
    private Integer valor;
    private Integer x;
    private Integer y;
    private Integer g;
    private Integer h;

    public Vertice(int x, int y) {
        this.x = x;
        this.y = y;
        valor = 0;
    }

    @Override
    public String toString() {
        return "Valor: " + valor + " (" + x + ", " + y + ")";
    }

    public ArrayList<Vertice> getAdjacentes() {
        return adjacentes;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getValor() {
        return valor;
    }

    public int getF() {
        return g + h;
    }

    public int getG() {
        return g;
    }

    public int getH() {
        return h;
    }

    public Vertice getAntecessor() {
        return antecessor;
    }

    public void setAdjacentes(ArrayList<ArrayList<Vertice>> matrizDeVertices) {
        if (valor != 1) {
            for (int i = x - 1; i < x + 2; i++) {
                for (int j = y - 1; j < y + 2; j++) {
                    if (i >= 0 && i <= matrizDeVertices.size() - 1 && j >= 0 && j <= matrizDeVertices.get(0).size() - 1
                            && matrizDeVertices.get(i).get(j).getValor() != 1 && (i != x || j != y))
                        adjacentes.add(matrizDeVertices.get(i).get(j));
                }
            }
        }
    }

    public void setValor(Integer valor) {
        this.valor = valor;
    }

    public void setG(Integer g) {
        this.g = g;
    }

    public void setH(Integer h) {
        this.h = h;
    }

    public void setAntecessor(Vertice antecessor) {
        this.antecessor = antecessor;
    }
}