package tad;

import model.Ocorrencia;

public class ListaSimplesmenteEncadeada<T> 
{
    
    private static class Node<T> 
    {
        T data;
        Node<T> next;

        Node(T data) 
        {
            this.data = data;
            this.next = null;
        }
    }

    private Node<T> head;
    private int size;

    public ListaSimplesmenteEncadeada() 
    {
        this.head = null;
        this.size = 0;
    }

    public void adicionar(T elemento) 
    {
        Node<T> newNode = new Node<>(elemento);
        if (head == null) 
        {
            head = newNode;
        } else 
        {
            Node<T> current = head;
            while (current.next != null) 
            {
                current = current.next;
            }
            current.next = newNode;
        }
        size++;
    }

    public int tamanho() 
    {
        return this.size;
    }

    public T obter(int index) 
    {
        if (index < 0 || index >= size) 
        {
            throw new IndexOutOfBoundsException("Índice fora dos limites da lista.");
        }
        Node<T> current = head;
        for (int i = 0; i < index; i++) 
        {
            current = current.next;
        }
        return current.data;
    }

    public boolean remover(T elemento) 
    {
        if (head == null) return false;

        if (head.data.equals(elemento)) 
        {
            head = head.next;
            size--;
            return true;
        }

        Node<T> current = head;
        while (current.next != null) 
        {
            if (current.next.data.equals(elemento)) 
            {
                current.next = current.next.next;
                size--;
                return true;
            }
            current = current.next;
        }
        return false;
    }

	
}