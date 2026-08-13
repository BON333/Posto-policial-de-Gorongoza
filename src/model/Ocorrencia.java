package model;

public class Ocorrencia {
    private String codigo;
    private Vitima vitima;
    private Suspeito suspeito;
    private String tipoCrime;
    private String descricao;
    private double prejuizoMaterial;
    private String status;

    public Ocorrencia(String codigo, Vitima vitima, Suspeito suspeito, String tipoCrime, String descricao, double prejuizoMaterial, String status) {
        this.codigo = codigo;
        this.vitima = vitima;
        this.suspeito = suspeito;
        this.tipoCrime = tipoCrime;
        this.descricao = descricao;
        this.prejuizoMaterial = prejuizoMaterial;
        this.status = status;
    }

    // Métodos Getters e Setters
    public String getCodigo() { return codigo; }
    public Vitima getVitima() { return vitima; }
    public Suspeito getSuspeito() { return suspeito; }
    public String getTipoCrime() { return tipoCrime; }
    public String getDescricao() { return descricao; }
    public double getPrejuizoMaterial() { return prejuizoMaterial; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    // Converte o objeto Ocorrência para uma linha estruturada separada por ';'
    public String toLinha() {
        return this.codigo + ";" + 
               this.vitima.getNome() + ";" + this.vitima.getContacto() + ";" + 
               this.suspeito.getNome() + ";" + this.suspeito.isConhecido() + ";" + this.suspeito.isRecorrente() + ";" + 
               this.tipoCrime + ";" + this.descricao + ";" + this.prejuizoMaterial + ";" + this.status;
    }
}