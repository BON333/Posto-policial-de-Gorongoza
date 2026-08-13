package view;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

import model.Ocorrencia;
import model.Vitima;
import model.Suspeito;
import model.Informador;
import service.PoliciaService;

public class Main 
{
    public static void main(String[] args) 
    {
        PoliciaService service = new PoliciaService();
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        int opcao = 0;

        do {
            System.out.println("\n=========================================");
            System.out.println("  SISTEMA DE GESTÃO DE OCORRÊNCIAS POLICIAIS");
            System.out.println("=========================================");
            System.out.println("1. Registrar Nova Ocorrência (Auto)");
            System.out.println("2. Atualizar Status / Ciclo de Vida do Auto");
            System.out.println("3. Cadastrar Informador da Polícia");
            System.out.println("4. Listar Informadores");
            System.out.println("5. Gerar Relatórios Analíticos e Histórico");
            System.out.println("6. Sair");
            System.out.print("Escolha uma opção: ");

            try {
                String entradaOpcao = reader.readLine();
                if (entradaOpcao == null || entradaOpcao.trim().isEmpty()) continue;
                opcao = Integer.parseInt(entradaOpcao.trim());

                switch (opcao) {
                    case 1:
                        System.out.print("Nome da Vítima: ");
                        String vNome = reader.readLine().trim();
                        while (vNome.isEmpty()) {
                            System.out.print("O nome não pode estar vazio. Digite novamente: ");
                            vNome = reader.readLine().trim();
                        }

                        // Validação de Contacto Moçambicano (9 dígitos, início com 82-87)
                        System.out.print("Contacto da Vítima (ex: 841234567): ");
                        String vCont = reader.readLine().trim();
                        while (!vCont.matches("^8[2-7]\\d{7}$")) {
                            System.err.print("Contacto inválido! Deve conter 9 dígitos e começar por 82, 83, 84, 85, 86 ou 87: ");
                            vCont = reader.readLine().trim();
                        }

                        System.out.print("Suspeito é conhecido? (S/N): ");
                        boolean conhec = reader.readLine().trim().equalsIgnoreCase("S");
                        String sNome = "Desconhecido";
                        boolean rec = false;
                        
                        if (conhec) {
                            System.out.print("Nome do Suspeito: ");
                            sNome = reader.readLine().trim();
                            System.out.print("Suspeito é Recorrente? (S/N): ");
                            rec = reader.readLine().trim().equalsIgnoreCase("S");
                        }

                        System.out.print("Tipo de Crime: ");
                        String crime = reader.readLine().trim();
                        System.out.print("Descrição da ocorrência: ");
                        String desc = reader.readLine().trim();

                        // Validação de prejuízo (impede valores negativos e quebras por texto)
                        double valor = -1;
                        while (valor < 0) {
                            try {
                                System.out.print("Prejuízo Material estimado (MT): ");
                                valor = Double.parseDouble(reader.readLine().trim());
                                if (valor < 0) {
                                    System.err.println("O prejuízo não pode ser um valor negativo!");
                                }
                            } catch (NumberFormatException e) {
                                System.err.println("Formato inválido! Introduza um número válido.");
                                valor = -1;
                            }
                        }

                        Ocorrencia nova = new Ocorrencia(
                            service.gerarProximoCodigo(), 
                            new Vitima(vNome, vCont), 
                            new Suspeito(sNome, conhec, rec), 
                            crime, desc, valor, "Aberto"
                        );
                        
                        service.registrarOcorrencia(nova);
                        System.out.println("Auto gravado com sucesso! Código atribuído: " + nova.getCodigo());
                        break;

                    case 2:
                        System.out.println("\n--- ALTERAÇÃO DO CICLO DE VIDA DO AUTO ---");
                        System.out.print("Introduza o Código do Auto (ex: AUTO-1): ");
                        String cod = reader.readLine().trim();
                        
                        // Validação: Impede códigos em branco
                        while (cod.isEmpty()) {
                            System.out.print("O código não pode estar vazio. Digite novamente: ");
                            cod = reader.readLine().trim();
                        }

                        // Procura a ocorrência ativa na Lista Encadeada manual
                        Ocorrencia busca = service.buscarOcorrenciaAtiva(cod);
                        
                        if (busca != null) {
                            System.out.println("\n[SUCESSO] Auto Ativo Encontrado!");
                            System.out.println("=========================================");
                            System.out.println("Código: " + busca.getCodigo());
                            System.out.println("Crime: "  + busca.getTipoCrime());
                            System.out.println("Vítima: " + busca.getVitima().getNome());
                            System.out.println("Status Atual: " + busca.getStatus());
                            System.out.println("=========================================");
                            
                            int stOp = 0;
                            // Ciclo robusto de validação para a escolha do novo estado
                            while (stOp < 1 || stOp > 3) {
                                try {
                                    System.out.println("Escolha o Novo Status para o Expediente:");
                                    System.out.println("1. Fechado   (Caso Resolvido no Posto)");
                                    System.out.println("2. Anulado   (Erro de Registo / Duplicação)");
                                    System.out.println("3. Descartado (Enviado para a Unidade Superior)");
                                    System.out.print("Opção: ");
                                    
                                    String entradaStatus = reader.readLine().trim();
                                    stOp = Integer.parseInt(entradaStatus);
                                    
                                    if (stOp < 1 || stOp > 3) {
                                        System.err.println("Erro: Escolha apenas as opções 1, 2 ou 3.\n");
                                    }
                                } catch (NumberFormatException e) {
                                    System.err.println("Erro: Entrada inválida! Digite apenas o número correspondente.\n");
                                    stOp = 0; 
                                }
                            }
                            
                            String novoStatus = "";
                            if (stOp == 1) novoStatus = "Fechado";
                            else if (stOp == 2) novoStatus = "Anulado";
                            else if (stOp == 3) novoStatus = "Descartado";
                            
                            // Aplica a transição e atualiza o arquivo TXT
                            service.atualizarStatus(cod, novoStatus);
                            System.out.println("[SUCESSO] O status do " + cod + " foi alterado para '" + novoStatus + "' e sincronizado no ficheiro.");
                            
                        } else {
                            System.err.println("[ERRO] O auto '" + cod + "' não foi encontrado nas ocorrências ativas.");
                            System.out.println("Dica: Verifique se o código está correto ou se o caso já não foi Fechado/Anulado anteriormente.");
                        }
                        break;

                    case 3:
                        String infId = "";
                        while (true) {
                            System.out.print("ID do Informador (ex: INF-01): ");
                            infId = reader.readLine().trim();
                            if (infId.isEmpty()) {
                                System.err.println("O ID não pode estar vazio.");
                                continue;
                            }
                            
                            // Validação de ID Único na Lista
                            if (service.buscarInformador(infId) != null) {
                                System.err.println("Erro: Já existe um informador com este ID!");
                            } else {
                                break;
                            }
                        }

                        System.out.print("Nome/Alcunha do Informador: ");
                        String infNome = reader.readLine().trim();
                        while (infNome.isEmpty()) {
                            System.out.print("O nome/alcunha não pode estar vazio: ");
                            infNome = reader.readLine().trim();
                        }

                        service.cadastrarInformador(new Informador(infId, infNome));
                        System.out.println("Informador registado com sucesso.");
                        break;

                    case 4:
                        service.listarInformadores();
                        break;

                    case 5:
                        service.exibirRelatorio();
                        break;

                    case 6:
                        System.out.println("A fechar o sistema e salvaguardando ficheiros textuais.");
                        break;
                        
                    default:
                        System.out.println("Opção inválida!");
                }
                
            } catch (IOException e) {
                System.out.println("Erro crítico de E/S ao ler os dados.");
            } catch (NumberFormatException e) {
                System.out.println("Erro: Escolha uma opção numérica válida do menu.");
            }
        } while (opcao != 6);
    }
}