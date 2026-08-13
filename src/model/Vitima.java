package model;

public class Vitima 
{
    private String nome;
    private String contacto;

    public Vitima(String nome, String contacto) 
    {
        this.nome = nome;
        this.contacto = contacto;
    }

    public String getNome() { return nome; }
    public String getContacto() { return contacto; }
}