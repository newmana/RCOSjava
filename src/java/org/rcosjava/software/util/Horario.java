package org.rcosjava.software.util;

import java.util.*;
import java.io.Serializable;

/**
 * Representa um hor�rio
 *
 */
public class Horario implements Serializable{
    private int segundo;
    private int minuto;
    private int hora;

    /**
     * Representa o formato hh:mi:ss
     */
    public static final int FORMATO1 = 1;

    /**
     * Representa o formato hhmiss
     */
    public static final int FORMATO2 = 2;

    /**
     * Representa o formato HHh e MMmin
     */
    public static final int FORMATO3 = 3;

    /**
     * Representa o formato HHh e MMmin e SSs
     */
    public static final int FORMATO4 = 4;

    /**
     * Construtor que inst�ncia o objeto com a data atual.
     */
    public Horario() {
        GregorianCalendar calendar = new GregorianCalendar();

        hora = calendar.get(Calendar.HOUR_OF_DAY);
        minuto = calendar.get(Calendar.MINUTE);
        segundo = calendar.get(Calendar.SECOND);
    }

    /**
     * Construtor que inst�ncia o objeto com a hora atual.
     */
    public Horario(String horarioStr) {
        Horario horario;
        horario = this.stringToHorario(horarioStr,3);
        this.hora = horario.getHora();
        this.minuto = horario.getMinuto();
        this.segundo = horario.getSegundo();
    }

    /**
     * Construtor da classe que instancia o objeto com segundo, minuto e hora
     *
     * @param segundo  segundo do hor�rio
     * @param minuto   minuto do hor�rio
     * @param hora     hora do hor�rio
     *
     * @exception HorarioInvalidaException  caso algum dos valores seja inv�lido
     *
     */
    public Horario(int segundo, int minuto,
                   int hora) throws HorarioInvalidoException {
        this.segundo = segundo;
        this.minuto = minuto;
        this.hora = hora;

        validaHorario(segundo, minuto, hora);
    }

    /**
     * Construtor da classe Horario, com strings segundo, minuto e hora
     *
     * @param segundo  segundo do hor�rio em formato texto
     * @param minuto   minuto do hor�rio em formato texto
     * @param hora     hora do hor�rio em formato texto
     *
     * @exception HorarioInvalidaException  caso algum dos valores seja inv�lido
     *
     */
    public Horario(String segundo, String minuto,
                   String hora) throws HorarioInvalidoException {
        try {
            this.segundo = Integer.parseInt(segundo);
            this.minuto = Integer.parseInt(minuto);
            this.hora = Integer.parseInt(hora);
            validaHorario(this.segundo, this.minuto, this.hora);
        } catch (NumberFormatException ne) {
            throw new HorarioInvalidoException(this.segundo, this.minuto,
                                               this.hora);
        }
    }

    /**
     * Compara este hor�rio com o hor�rio passado
     *
     * @return  1 se for igual, <br>
     * -1 caso contr�rio
     *
     */
    public int compara(Horario horario) {
        int retorno = 0;

        if (hora > horario.getHora()) {
            retorno = 1;
        } else if (hora < horario.getHora()) {
            retorno = -1;
        } else {
            if (minuto > horario.getMinuto()) {
                retorno = 1;
            } else if (minuto < horario.getMinuto()) {
                retorno = -1;
            } else {
                if (segundo > horario.getSegundo()) {
                    retorno = 1;
                } else if (segundo < horario.getSegundo()) {
                    retorno = -1;
                }
            }
        }

        return retorno;
    }

    /**
     * Compara se horaInicio � menor do que a horaFim
     *
     * @return  true se horaInicio for menor do que horaFim, <br>
     *          false caso contr�rio
     *
     */
    public boolean comparaHoras(Horario horario) {
        boolean retorno = false;

        if (hora > horario.getHora()) {
            retorno = false;
        } else {
            if (hora < horario.getHora()) {
                retorno = true;
            } else {
                if (minuto > horario.getMinuto()) {
                    retorno = false;
                } else {
                    if (minuto < horario.getMinuto()) {
                        retorno = true;
                    } else {
                        if (segundo >= horario.getSegundo()) {
                            retorno = false;
                        } else {
                            retorno = true;
                        }
                    }
                }
            }
        }

        return retorno;
    }

    /**
     * Retorna a representa��o string do hor�rio no formato indicado
     *
     * @param formato  formato de retorno do hor�rio
     *
     * @return  representa��o string do hor�rio
     *
     */
    public String format(int formato) {
        String horario = null;

        try {
            horario = format(this, formato);
        } catch (HorarioInvalidoException e) {
            horario = "";
        }

        return horario;
    }

    /**
     * Retorna a representa��o string da hor�rio no formato indicado
     *
     * @param horario  horario a ser formatado
     * @param formato  formato de retorno da hor�rio
     *
     * @return  representa��o string do hor�rio
     *
     * throws HorarioInvalidaException  exce��o levantada caso algum dos valores
     * seja inv�lido
     *
     */
    public static String format(Horario horario,
                                int formato) throws HorarioInvalidoException {
        String segundoStr = "";
        String minutoStr = "";
        String horaStr = "";
        String texto = null;

        try {
            segundoStr = String.valueOf(horario.getSegundo());
            minutoStr = String.valueOf(horario.getMinuto());
            horaStr = String.valueOf(horario.getHora());

            if (segundoStr.length() < 2) {
                segundoStr = "0" + segundoStr;
            }

            if (minutoStr.length() < 2) {
                minutoStr = "0" + minutoStr;
            }

            if (horaStr.length() < 2) {
                horaStr = "0" + horaStr;
            }
        } catch (NumberFormatException e) {
            throw new HorarioInvalidoException(e, horario);
        }

        switch (formato) {

        case (FORMATO1):
            texto = horaStr + ":" + minutoStr + ":" + segundoStr;

            break;

        case (FORMATO2):
            texto = horaStr + minutoStr + segundoStr;

            break;

        case (FORMATO3):
            texto = horaStr + ":" + minutoStr;

            break;

        default:
            texto = null;

            break;
        }

        return texto;
    }

    /**
     * Retorna a hora do hor�rio
     *
     * @return  hora do hor�rio
     *
     */
    public int getHora() {
        return hora;
    }

    /**
     * Retorna o minuto do hor�rio
     *
     * @return  minuto do hor�rio
     *
     */
    public int getMinuto() {
        return minuto;
    }

    /**
     * Retorna o segundo do hor�rio
     *
     * @return  segundo do hor�rio
     *
     */
    public int getSegundo() {
        return segundo;
    }

    /**
     * Transforma um tempo em milisegundos
     *
     * @param tempo  Tempo em milisegundos
     * @param formato  Formato de retorno do hor�rio
     *
     * @return  O hor�rio no formato informado
     *
     */
    public static String milisegundosParaHora(long tempo, int formato) {
        String hora = "";
        int secs = (int) tempo / 1000;

        if (tempo == 0) {
            hora = "0";
        } else {
            int[] ret = new int[3];

            // calcula n�mero de horas, minutos e segundos
            ret[0] = secs / 3600;
            secs = secs % 3600;
            ret[1] = secs / 60;
            secs = secs % 60;
            ret[2] = secs;

            if (ret[0] != 0) {
                hora += ret[0] + "h";
            }

            if (ret[1] != 0) {
                hora += ret[1] + "min";
            }

            if ((formato != FORMATO3) && (ret[2] != 0)) {
                hora += ret[2] + "s";
            }
        }       // fim do if (tempo == 0)

        return hora;
    }

    /**
     * Transforma string em hor�rio
     *
     * @param horarioStr  hor�rio em forma de texto
     * @param formato  formato em que est� o hor�rio
     *
     * @return  o hor�rio como uma inst�ncia da classe Horario
     *
     * throws HorarioInvalidoException  exce��o levantada caso algum dos valores
     * seja inv�lido
     *
     */
    public static Horario stringToHorario(String horarioStr, int formato) {
//            throws HorarioInvalidoException {
        int segundo = 0;
        int minuto = 0;
        int hora = 0;
        String segundoStr = null;
        String minutoStr = null;
        String horaStr = null;
        Horario horario = null;

        try {
            switch (formato) {

            case (Horario.FORMATO1):
                horaStr = horarioStr.substring(0, 2);
                minutoStr = horarioStr.substring(3, 5);
                segundoStr = horarioStr.substring(6, 8);

                break;

            case (Horario.FORMATO2):
                segundoStr = horarioStr.substring(0, 2);
                minutoStr = horarioStr.substring(2, 4);
                horaStr = horarioStr.substring(4, 6);

                break;

            case (Horario.FORMATO3):
                horaStr = horarioStr.substring(0, 2);
                minutoStr = horarioStr.substring(3, 5);
                segundoStr = "00";

                break;

            default:
                horario = null;

                break;
            }

            horario = new Horario(segundoStr, minutoStr, horaStr);
        } catch (Exception e) {
//            throw new HorarioInvalidoException(horarioStr);
        }

        return horario;
    }

    /**
     * Valida um hor�rio (segundo, minuto e hora)
     *
     * @param segundo  segundo do hor�rio
     * @param minuto   minuto do hor�rio
     * @param hora     hora do hor�rio
     *
     * @exception HorarioInvalidaException  caso algum dos valores seja inv�lido
     *
     */
    private void validaHorario(int segundo, int minuto,
                               int hora) throws HorarioInvalidoException {
        if (!((segundo >= 0) && (segundo <= 59) && (minuto >= 0)
                && (minuto <= 59) && (hora >= 0) && (hora <= 23))) {
            throw new HorarioInvalidoException(segundo, minuto, hora);
        }
    }

    public String toString() {
        String horario = "";
        try {
            horario = format(this, FORMATO3);
        } catch (HorarioInvalidoException hie) {
        }
        return horario;
    }

}



/*--- formatting done in "CESAR" style on 09-07-2000 ---*/

