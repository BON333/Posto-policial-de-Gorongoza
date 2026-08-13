package service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import model.Ocorrencia;
import model.Informador;
import tad.ListaSimplesmenteEncadeada;

public class PoliciaService {
    private ListaSimplesmenteEncadeada<Ocorrencia> listaAtiva = new ListaSimplesmenteEncadeada<Ocorrencia>();
    private ListaSimplesmenteEncadeada<Ocorrencia> historicoGeral = new ListaSimplesmenteEncadeada<Ocorrencia>();
    private ListaSimplesmenteEncadeada<Informador> listaInformadores = new ListaSimplesmenteEncadeada<Informador>();

    // Definição de limites realistas operacionais
    private final int LIMITE_MINIMO = 2;
    private final int LIMITE_MAXIMO = 20; 
    private int contadorSequencial = 1;

    public PoliciaService() {
        carregarFicheiroTexto();
    }

    public void registrarOcorrencia(Ocorrencia o) {
        listaAtiva.adicionar(o);
        historicoGeral.adicionar(o);
        gravarFicheiroTexto(); 
        verificarAlertas();
    }

    public void cadastrarInformador(Informador inf) {
        listaInformadores.adicionar(inf);
        System.out.println("[SISTEMA] Informador registado em memória.");
    }

    public String gerarProximoCodigo() {
        return "AUTO-" + (contadorSequencial++);
    }

    public Ocorrencia buscarOcorrenciaAtiva(String codigo) {
        for (int i = 0; i < listaAtiva.tamanho(); i++) {
            Ocorrencia o = listaAtiva.obter(i);
            if (o.getCodigo().equalsIgnoreCase(codigo)) {
                return o;
            }
        }
        return null;
    }

    // Validação de ID Único para informadores
    public Informador buscarInformador(String id) {
        for (int i = 0; i < listaInformadores.tamanho(); i++) {
            Informador inf = listaInformadores.obter(i);
            if (inf.getId().equalsIgnoreCase(id)) {
                return inf;
            }
        }
        return null;
    }

    public void atualizarStatus(String codigo, String novoStatus) {
        Ocorrencia o = buscarOcorrenciaAtiva(codigo);
        if (o != null) {
            o.setStatus(novoStatus);
            if (novoStatus.equalsIgnoreCase("Anulado") || novoStatus.equalsIgnoreCase("Descartado") || novoStatus.equalsIgnoreCase("Fechado")) {
                listaAtiva.remover(o);
            }
            gravarFicheiroTexto(); 
            verificarAlertas();
        }
    }

    public int getQuantidadeAtivas() {
        int cont = 0;
        for (int i = 0; i < listaAtiva.tamanho(); i++) {
            if (listaAtiva.obter(i).getStatus().equalsIgnoreCase("Aberto")) {
                cont++;
            }
        }
        return cont;
    }

    // Sistema de monitorização de limites (Requisito E do Enunciado)
    public void verificarAlertas() {
        int ativas = getQuantidadeAtivas();
        System.out.println("\n=== [SISTEMA] Monitoria de Carga Operacional: " + ativas + " casos ativos ===");
        
        if (ativas >= LIMITE_MAXIMO) {
            System.err.println("!!! ALERTA MÁXIMO: Saturação de contingente! Capacidade de resposta do Posto da Gorongoza excedida. Transfira casos para a Unidade Superior. !!!");
        } else if (ativas <= LIMITE_MINIMO) {
            System.out.println("??? ALERTA MÍNIMO: Baixo índice de atividade registada. Verifique os piquetes e patrulhas. ???");
        }
    }

    public void exibirRelatorio() {
        System.out.println("\n=========================================");
        System.out.println("   RELATÓRIO ANALÍTICO - POSTO GORONGOZA   ");
        System.out.println("=========================================");
        double totalPrejuizo = 0;

        System.out.println("\n--- Histórico de todos os casos registados ---");
        if (historicoGeral.tamanho() == 0) {
            System.out.println("Nenhum registo encontrado no ficheiro 'ocorrencias.txt'.");
        }
        
        for (int i = 0; i < historicoGeral.tamanho(); i++) {
            Ocorrencia o = historicoGeral.obter(i);
            System.out.println("[" + o.getCodigo() + "] Crime: " + o.getTipoCrime() + " | Status: " + o.getStatus() + " | Vítima: " + o.getVitima().getNome());
            totalPrejuizo += o.getPrejuizoMaterial();
        }
        System.out.println("\nPrejuízo Material Total Avaliado: " + totalPrejuizo + " MT");
    }

    public void listarInformadores() {
        System.out.println("\n--- Lista de Informadores Colaboradores ---");
        if (listaInformadores.tamanho() == 0) {
            System.out.println("Nenhum cadastrado.");
        }
        for (int i = 0; i < listaInformadores.tamanho(); i++) {
            Informador inf = listaInformadores.obter(i);
            System.out.println("ID: " + inf.getId() + " | Nome: " + inf.getNome());
        }
    }

    public void gravarFicheiroTexto() {
        String nomeFicheiro = "ocorrencias.txt";
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(nomeFicheiro))) {
            for (int i = 0; i < historicoGeral.tamanho(); i++) {
                Ocorrencia o = historicoGeral.obter(i);
                bw.write(o.toLinha());
                bw.newLine();
            }
            System.out.println("[SISTEMA] Ficheiro '" + nomeFicheiro + "' sincronizado com sucesso.");
        } catch (IOException e) {
            System.out.println("Erro crítico ao gravar os dados no ficheiro: " + e.getMessage());
        }
    }

    public void carregarFicheiroTexto() {
        String nomeFicheiro = "ocorrencias.txt";
        try (BufferedReader br = new BufferedReader(new FileReader(nomeFicheiro))) {
            String linha;
            int maiorID = 0;
            
            while ((linha = br.readLine()) != null) {
                String[] dados = linha.split(";");
                if (dados.length < 10) continue;

                String codigo = dados[0];
                model.Vitima vitima = new model.Vitima(dados[1], dados[2]);
                model.Suspeito suspeito = new model.Suspeito(dados[3], Boolean.parseBoolean(dados[4]), Boolean.parseBoolean(dados[5]));
                String tipoCrime = dados[6];
                String descricao = dados[7];
                double prejuizo = Double.parseDouble(dados[8]);
                String status = dados[9];

                Ocorrencia o = new Ocorrencia(codigo, vitima, suspeito, tipoCrime, descricao, prejuizo, status);
                historicoGeral.adicionar(o);

                if (status.equalsIgnoreCase("Aberto")) {
                    listaAtiva.adicionar(o);
                }

                try {
                    int numCodigo = Integer.parseInt(codigo.replace("AUTO-", ""));
                    if (numCodigo > maiorID) maiorID = numCodigo;
                } catch (NumberFormatException e) {}
            }
            this.contadorSequencial = maiorID + 1;
            System.out.println("[SISTEMA] Base de dados restaurada do ficheiro com sucesso.");
        } catch (IOException e) {
            System.out.println("[INFO] Nenhum ficheiro '" + nomeFicheiro + "' detectado. Um novo será criado automaticamente.");
        }
    }
}