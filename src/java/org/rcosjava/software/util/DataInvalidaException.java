package org.rcosjava.software.util;

/**
 * Exceção que representa incorreto preenchimento de uma Data
 */
public class DataInvalidaException extends Exception {
    private Object objetoGeradorDoErro;

    /**
     * Construtor da classe, cria uma instância da data em formato texto
     *
     * @param dia  dia da data que gerou o erro
     * @param mes  mes da data que gerou o erro
     * @param ano  ano da data que gerou o erro
     *
     */
    public DataInvalidaException(int dia, int mes, int ano) {
        super("O campo 'Data' foi preenchido incorretamente!");

        this.objetoGeradorDoErro = dia + "/" + mes + "/" + ano;
    }

    /**
     * Construtor da classe, cria uma instância passando a exceção gerada e o
     * objeto que gerou a exceção
     *
     * @param exception  Exceção gerada
     * @param objetoGeradorDoErro  Objeto que gerou a exceção
     *
     */
    public DataInvalidaException(Exception excecao,
                                 Object objetoGeradorDoErro) {
        super(""+excecao);

        this.objetoGeradorDoErro = objetoGeradorDoErro;
    }

    /**
     * Construtor da classe, cria uma instância passando o
     * objeto que gerou a exceção
     *
     * @param objetoGeradorDoErro  Objeto que gerou a exceção
     *
     */
    public DataInvalidaException(Object objetoGeradorDoErro) {
        this(null, objetoGeradorDoErro);
    }

    /**
     * Construtor da classe, cria uma instância passando o objeto que gerou a
     * exceção
     *
     * @param data  data que gerou o erro
     *
     */
    public DataInvalidaException(String data) {
        super("O campo 'Data' foi preenchido incorretamente!");

        this.objetoGeradorDoErro = data;
    }

    /**
     * Retorna o objeto que gerou o erro
     *
     * @return objeto que gerou o erro
     *
     */
    public Object getObjetoGeradorDoErro() {
        return objetoGeradorDoErro;
    }

}



/*--- formatting done in "CESAR" style on 09-07-2000 ---*/

