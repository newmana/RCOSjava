package org.rcosjava.software.util;

/**
 * Exce��o que representa incorreto preenchimento de um Hor�rio
 *
 */
public class HorarioInvalidoException extends Exception{
    private Object objetoGeradorDoErro;

    /**
     * Construtor da classe, cria uma inst�ncia do hor�rio em formato texto
     *
     * @param segundo  segundo do hor�rio que gerou o erro
     * @param minuto   minuto  do hor�rio que gerou o erro
     * @param hora     hoara   do hor�rio que gerou o erro
     *
     */
    public HorarioInvalidoException(int segundo, int minuto, int hora) {
        super("O campo 'Hor�rio' foi preenchido incorretamente(" + segundo
              + "," + minuto + "," + segundo + ")!");

        this.objetoGeradorDoErro = segundo + ":" + minuto + ":" + hora;
    }

    /**
     * Construtor da classe, cria uma inst�ncia passando a exce��o gerada e o
     * objeto que gerou a exce��o
     *
     * @param exception  Exce��o gerada
     * @param objetoGeradorDoErro  Objeto que gerou a exce��o
     *
     */
    public HorarioInvalidoException(Exception excecao,
                                    Object objetoGeradorDoErro) {
        super(""+excecao);

        this.objetoGeradorDoErro = objetoGeradorDoErro;
    }

    /**
     * Construtor da classe, cria uma inst�ncia passando o
     * objeto que gerou a exce��o
     *
     * @param objetoGeradorDoErro  Objeto que gerou a exce��o
     *
     */
    public HorarioInvalidoException(Object objetoGeradorDoErro) {
        this(null, objetoGeradorDoErro);
    }

    /**
     * Construtor da classe, cria uma inst�ncia passando o hor�rio gerador do erro
     *
     * @param horario  horario que gerou o erro
     *
     */
    public HorarioInvalidoException(String horario) {
        super("O campo 'Horario' foi preenchido incorretamente!");

        this.objetoGeradorDoErro = horario;
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

