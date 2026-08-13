package model;

public class Suspeito 
{
    private String nome;
    private boolean conhecido;
    private boolean recorrente; // true = recorrente, false = primário

    public Suspeito(String nome, boolean conhecido, boolean recorrente) 
    {
        this.nome = conhecido ? nome : "Desconhecido";
        this.conhecido = conhecido;
        this.recorrente = recorrente;
    }

    public String getNome() { return nome; }
    public boolean isConhecido() { return conhecido; }
    public boolean isRecorrente() { return recorrente; }
}