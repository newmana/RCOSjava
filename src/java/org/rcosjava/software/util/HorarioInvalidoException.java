package org.rcosjava.software.util;

/**
 * Exceção que representa incorreto preenchimento de um Horário
 *
 */
public class HorarioInvalidoException extends Exception{
    private Object objetoGeradorDoErro;

    /**
     * Construtor da classe, cria uma instância do horário em formato texto
     *
     * @param segundo  segundo do horário que gerou o erro
     * @param minuto   minuto  do horário que gerou o erro
     * @param hora     hoara   do horário que gerou o erro
     *
     */
    public HorarioInvalidoException(int segundo, int minuto, int hora) {
        super("O campo 'Horário' foi preenchido incorretamente(" + segundo
              + "," + minuto + "," + segundo + ")!");

        this.objetoGeradorDoErro = segundo + ":" + minuto + ":" + hora;
    }

    /**
     * Construtor da classe, cria uma instância passando a exceção gerada e o
     * objeto que gerou a exceção
     *
     * @param exception  Exceção gerada
     * @param objetoGeradorDoErro  Objeto que gerou a exceção
     *
     */
    public HorarioInvalidoException(Exception excecao,
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
    public HorarioInvalidoException(Object objetoGeradorDoErro) {
        this(null, objetoGeradorDoErro);
    }

    /**
     * Construtor da classe, cria uma instância passando o horário gerador do erro
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

