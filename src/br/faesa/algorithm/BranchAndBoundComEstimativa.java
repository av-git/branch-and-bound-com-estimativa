package br.faesa.algorithm;

import java.util.Iterator;
import java.util.PriorityQueue;

/**
 * Classe que representa o algortimo 
 * Branch and Bound com Estimativa
 * 
 * @author Ivan Nicoli
 * @author Loiane Groner
 * @author Luiz Gustavo Ribeiro Pagani
 *
 */
public class BranchAndBoundComEstimativa {
	/**
	 * N� Raiz
	 */
	No noRaiz;
	/**
	 * V�rtice de Origem
	 */
	int origem;	
	/**
	 * V�rtice de Destino
	 */
	int destino;
	private String[] nomes;
	/*String[] nomes = { "S", "A", "D", "B", "E", "C", "F", "G" };*/
	/**
	 * Fila de n�s priorit�rios a serem percorridos para encontrar o n� objetivo
	 */
	PriorityQueue<No> fila;
	/**
	 * Matriz de Adjac�ncia do Grafo
	 */
	private int[][] matrizAdjacencia;
	/*int[][] matrizAdjacencia = {
	{ 0, 3, 4, 0, 0, 0, 0, 0},
	{ 3, 0, 5, 1, 0, 0, 0, 0},
	{ 4, 5, 0, 0, 2, 0, 0, 0},
	{ 0, 1, 0, 0, 5, 4, 0, 0},
	{ 0, 0, 2, 5, 0, 0, 4, 0},
	{ 0, 0, 0, 4, 0, 0, 0, 0},
	{ 0, 0, 0, 0, 4, 0, 0, 3},
	{ 0, 0, 0, 0, 0, 0, 3, 0},
	};*/	
	/**
	 * Vetor com o valor das Estimativas 
	 */
	private double[] estimativas;
	/*double[] estimativas = { 11,  10.4,  8.9, 6.7, 6.9, 4, 3, 0};*/		
	
	/**
	 * Construtor que inicializa os objetos com as informa��es
	 * necess�rias
	 * @param matrizAdjacencia - matriz de adjac�ncia
	 * @param estimativas - vetor com as estimativas
	 * @param origem - v�rtice de origem
	 * @param destino - v�rtice de destino
	 */
	public BranchAndBoundComEstimativa(int[][] matrizAdjacencia, double[] estimativas, int origem, int destino, String[] nomes)
	{
		this.matrizAdjacencia = matrizAdjacencia;	
		this.estimativas = estimativas;
		this.origem = origem;
		this.destino = destino;
		this.nomes = nomes;
		
		// Cria o n� raiz
		this.noRaiz = this.criaNo(this.origem, null);
		/*
		 * Cria uma nova fila de prioridade, com capacidade inicial de 10
		 * e usando a classe NoComparator para definir as prioridades. 
		 */ 		
		fila = new PriorityQueue<No>(10, new No.NoComparator());
	}	

	private void criaFilhos(No pai) {
		for (int i = 0; i < this.matrizAdjacencia.length; i++) {			
			if (this.matrizAdjacencia[pai.i][i] > 0) {
				/*
				 * b. Rejeitar todos novos caminhos com ciclos (loop);
				 */
				if (!this.verticeExistente(i, pai)) {
					No filho = this.criaNo(i, pai);
				
					pai.filhos.add(filho);								
				}
			}						
		}
	}	
	/**
	 * Este algoritmo faz com que seja criada a �rvore com todos os v�rtices poss�veis. 
	 * @param pai n� raiz
	 */
	private void criaArvoreToda(No pai) {
		for (int i = 0; i < this.matrizAdjacencia.length; i++) {			
			if (this.matrizAdjacencia[pai.i][i] > 0) {
				/*
				 * b. Rejeitar todos novos caminhos com ciclos (loop);
				 */
				if (!this.verticeExistente(i, pai)) {
					No filho = this.criaNo(i, pai);
					
					pai.filhos.add(filho);
					
					criaArvoreToda(filho);					
				}
			}						
		}
	}		
	/**
	 * Verifica se um v�rtice j� existe na �rvore, para evitar loops.
	 * @param i �ndice do v�rtice
	 * @param no n� pai
	 * @return true se o v�rtice j� existir na �rvore
	 */
	private boolean verticeExistente(int i, No no) {
		while (no != null) {
			if (no.pai != null && no.pai.i == i)
			{
				return true;
			}
			no = no.pai;
		}
		return false;
	}
	
	/**
	 * Cria um n�. Se for n� raiz, deve passar pai nulo. 
	 * @param i �ndice do n�
	 * @param pai n� pai
	 * @return novo n�
	 */
	private No criaNo(int i, No pai) {
		No no = new No();
		no.i = i;
		if (pai == null) {
			no.custoPercorrido = 0;			
		}
		else {
			no.custoPercorrido = pai.custoPercorrido + this.matrizAdjacencia[pai.i][i];
		}
		no.pai = pai;
		no.custoRestante = this.estimativas[no.i]; 
		no.custoComEstimativa = no.custoPercorrido + no.custoRestante;
		no.nome = this.nomes[no.i];
		return no;
	}
	
	public String execute()
	{		
		/*
		 * 1. Construir uma fila (queue) com 1 elemento consistindo de um caminho de comprimento
		 * zero contendo somente o n� raiz (root);
		 */
		fila.offer(this.noRaiz);
		
		/*
		 * 2. Repetir at� encontrar o primeiro caminho da fila que contenha o n�-objetivo ou at� que a
		 * fila esteja vazia
		 */
		while (fila.size() != 0 && fila.peek().i != this.destino) {
			System.out.println(fila.peek().nome + " - " + fila.peek().custoComEstimativa);
			/*
			 * a. �Remover� o primeiro caminho da fila; criar novos caminhos estendendo o
			 * primeiro caminho para todos os n�s vizinhos do n� terminal;
			 * 
			 * A etapa "b" est� sendo feita dentro do m�todo criaFilhos.
			 */
			No noCorrente = fila.poll();
			this.criaFilhos(noCorrente);

			/*
			 * c. Adicionar � fila os novos caminhos restantes, se existirem;
			 * 
			 * A etapa "d" est� sendo feita dentro da classe No.NoComparator
			 */
			if (noCorrente.filhos.size() > 0) {
				for (Iterator<No> iter = noCorrente.filhos.iterator(); iter.hasNext();) {
					No element = iter.next();
					
					fila.offer(element);
				}
			}		
		}
		
		return noRaiz.exibeArvore();
	}
	
}
