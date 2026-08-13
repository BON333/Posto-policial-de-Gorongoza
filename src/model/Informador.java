package model;

public class Informador 
{
    private String id;
    private String nome;

    public Informador(String id, String nome) 
    {
        this.id = id;
        this.nome = nome;
    }

    public String getId() { return id; }
    public String getNome() { return nome; }
}