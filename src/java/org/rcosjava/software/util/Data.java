package org.rcosjava.software.util;

import java.util.*;
import java.lang.*;

/**
 * Representa uma Data
 *
 */
public class Data {
    private Horario horario;
    private int dia;
    private int mes;
    private int ano;

    /**
     * Representa o formato dd/mm/aaaa
     */
    public static final int FORMATO1 = 1;

    /**
     * Representa o formato dd/mm/aaaa:hh:mi:ss
     */
    public static final int FORMATO2 = 2;

    /**
     * Representa o formato ddmmaaaa
     */
    public static final int FORMATO3 = 3;

    /**
     * Representa o formato ddmmaaaahhmiss
     */
    public static final int FORMATO4 = 4;

    /**
     * Representa o formato dd/mm/aaaa com base no aaaa/mm/dd
     */
    public static final int FORMATO5 = 5;

    /**
     * Construtor que instância o objeto com a data atual.
     *
     */
    public Data() {
        GregorianCalendar calendar = new GregorianCalendar();

        dia = calendar.get(Calendar.DATE);
        mes = calendar.get(Calendar.MONTH) + 1;
        ano = calendar.get(Calendar.YEAR);
        horario = new Horario();
    }

    /**
     * Construtor que recebe como parâmeto os inteiros
     * dia, mes e ano e instância o objeto com os mesmos.
     *
     * @param dia  dia
     * @param mes  mês
     * @param ano  ano
     *
     * throws DataInvalidaException  exceção levantada caso algum dos valores
     * seja invalido
     *
     */
    public Data(int dia, int mes, int ano) throws DataInvalidaException {
        this.dia = dia;
        this.mes = mes;
        this.ano = ano;
        this.horario = null;

        validaData(dia, mes, ano);
    }


    /**
     * Construtor que recebe como parâmeto um java.sql.Date
     *
     * @param data
     *
     * throws DataInvalidaException  exceção levantada caso algum dos valores
     * seja invalido
     *
     */
    public Data(java.util.Date data) throws DataInvalidaException {

        try {
            GregorianCalendar c = new GregorianCalendar();
            c.setTime(data);

            this.dia = c.get(c.DAY_OF_MONTH);
            this.mes = c.get(c.MONTH)+1;
            this.ano = c.get(c.YEAR);
            this.horario = new Horario(c.get(c.SECOND),
                                       c.get(c.MINUTE),
                                       c.get(c.HOUR));

            validaData(dia, mes, ano);

        } catch (HorarioInvalidoException hie) {
            throw new DataInvalidaException(dia, mes, ano);
        }
    }

    /**
     * Construtor que recebe como parâmetro os inteiros
     * segundo,minuto,hora,dia, mes e ano e instância o objeto com os mesmos.
     *
     * @param segundo  segundo
     * @param minuto   minuto
     * @param hora     hora
     * @param dia      dia
     * @param mes      mês
     * @param ano      ano
     *
     * throws DataInvalidaException  exceção levantada caso algum dos valores
     * seja invalido
     *
     */
    public Data(int segundo, int minuto, int hora, int dia, int mes,
                int ano) throws DataInvalidaException {
        try {
            this.dia = dia;
            this.mes = mes;
            this.ano = ano;
            this.horario = new Horario(segundo, minuto, hora);

            validaData(dia, mes, ano);
        } catch (HorarioInvalidoException hie) {
            throw new DataInvalidaException(dia, mes, ano);
        }
    }

    /**
     * Construtor que recebe como parâmetro um string no formato dia/mes/ano.
     *
     * @param dataStr  O String representando a data no formato dia/mes/ano
     *
     * throws DataInvalidaException  exceção levantada caso algum dos valores
     * seja invalido
     *
     */
    public Data(String dataStr) throws DataInvalidaException {
        try {
            StringTokenizer tokenizerData = new StringTokenizer(dataStr, "/");

            this.dia = Integer.parseInt(tokenizerData.nextElement().toString());
            this.mes = Integer.parseInt(tokenizerData.nextElement().toString());
            this.ano = Integer.parseInt(tokenizerData.nextElement().toString());
            this.horario = new Horario();
        } catch (Exception e) {
            throw new DataInvalidaException(dia, mes, ano);
        }

        validaData(dia, mes, ano);
    }

    /**
     * Construtor que recebe como parâmetro os strings
     * dia, mes e ano e instância o objeto com os mesmos.
     *
     * @param dia      dia
     * @param mes      mês
     * @param ano      ano
     *
     * throws DataInvalidaException  exceção levantada caso algum dos valores
     * seja invalido
     *
     */
    public Data(String diaStr, String mesStr,
                String anoStr) throws DataInvalidaException {
        try {
            this.dia = Integer.parseInt(diaStr);
            this.mes = Integer.parseInt(mesStr);
            this.ano = Integer.parseInt(anoStr);
            this.horario = null;
        } catch (Exception e) {
            throw new DataInvalidaException(dia, mes, ano);
        }

        validaData(dia, mes, ano);
    }

    /**
     * Construtor que recebe como parâmetro os strings
     * segundo,minuto,hora,dia, mes e ano e instância o objeto com os mesmos.
     *
     * @param segundo  segundo
     * @param minuto   minuto
     * @param hora     hora
     * @param dia      dia
     * @param mes      mês
     * @param ano      ano
     *
     * throws DataInvalidaException  exceção levantada caso algum dos valores
     * seja invalido
     *
     */
    public Data(String segundoStr, String minutoStr, String horaStr,
                String diaStr, String mesStr,
                String anoStr) throws DataInvalidaException {
        try {
            this.dia = Integer.parseInt(diaStr);
            this.mes = Integer.parseInt(mesStr);
            this.ano = Integer.parseInt(anoStr);
            this.horario = new Horario(Integer.parseInt(segundoStr),
                                       Integer.parseInt(minutoStr),
                                       Integer.parseInt(horaStr));
        } catch (Exception e) {
            throw new DataInvalidaException(dia, mes, ano);
        }

        validaData(dia, mes, ano);
    }

    /**
     * Acrescenta um determinado número de dias a data
     *
     * @param data  data a ser acrescentada a número de dias
     * @param dias  número de dias a acrescentar
     *
     */
    public static void addDias(Data data, int dias) {
        Data novaData;

        novaData = data;

        while (dias > 0) {
            novaData = novaData.proximaData();
            dias = dias - 1;
        }

        data.dia = novaData.dia;
        data.mes = novaData.mes;
        data.ano = novaData.ano;
        data.horario = novaData.horario;
    }

    /**
     * Verifica se a instância de Data passada como parâmetro
     * é igual, maior ou menor a instância da qual o método foi chamado.
     *
     * @param Data  data a ser comparada
     *
     * @return  -1 se for maior, <br>
     * 0 se for igual, <br>
     * 1 se for menor
     */
    public int compara(Data data) {
        int retorno = 0;

        if (ano > data.getAno()) {
            retorno = 1;
        } else if (ano < data.getAno()) {
            retorno = -1;
        } else {
            if (mes > data.getMes()) {
                retorno = 1;
            } else if (mes < data.getMes()) {
                retorno = -1;
            } else {
                if (dia > data.getDia()) {
                    retorno = 1;
                } else if (dia < data.getDia()) {
                    retorno = -1;
                }
            }
        }

        return retorno;
    }

    /**
     * Retorna a data do dia anterior
     *
     * @return  data do dia anterior
     *
     */
    public Data dataAnterior() {
        Data dataRetorno = null;
        int diaDataAnterior;
        int mesDataAnterior;
        int anoDataAnterior;

        if (dia > 1) {
            diaDataAnterior = dia - 1;
            mesDataAnterior = mes;
            anoDataAnterior = ano;
        } else if (dia == 1 && (mes != 1)) {
            diaDataAnterior = numeroDeDiasDoMes(mes - 1);
            mesDataAnterior = mes - 1;
            anoDataAnterior = ano;
        } else {
            diaDataAnterior = 31;
            mesDataAnterior = 12;
            anoDataAnterior = ano - 1;
        }

        try {
            dataRetorno = new Data(diaDataAnterior, mesDataAnterior,
                                   anoDataAnterior);
        } catch (DataInvalidaException e) {}

        return dataRetorno;
    }

    /**
     * Retorna a data do dia anterior relativa a uma dada data
     *
     * @param dataReferencia  A data de referência
     *
     * @return  data do dia anterior em relação à data de referência
     *
     */
    public Data dataAnterior(Data dataReferencia) {
        Data dataRetorno = null;
        int diaDataAnterior;
        int mesDataAnterior;
        int anoDataAnterior;
        int diaReferencia = dataReferencia.getDia();
        int mesReferencia = dataReferencia.getMes();
        int anoReferencia = dataReferencia.getAno();

        if (diaReferencia > 1) {
            diaDataAnterior = diaReferencia - 1;
            mesDataAnterior = mesReferencia;
            anoDataAnterior = anoReferencia;
        } else if (diaReferencia == 1 && (mesReferencia != 1)) {
            diaDataAnterior = numeroDeDiasDoMes(mesReferencia - 1);
            mesDataAnterior = mesReferencia - 1;
            anoDataAnterior = anoReferencia;
        } else {
            diaDataAnterior = 31;
            mesDataAnterior = 12;
            anoDataAnterior = anoReferencia - 1;
        }

        try {
            dataRetorno = new Data(diaDataAnterior, mesDataAnterior,
                                   anoDataAnterior);
        } catch (DataInvalidaException e) {}

        return dataRetorno;
    }

    /**
     * Retorna a diferença em dias de duas datas
     *
     * @param dataInicio  data inicial
     * @param dataFim     data final
     *
     * @return  diferença em dias
     *
     */
    public static int diferencaEmDias(Data dataInicio, Data dataFim) {
        int diferenca = 0;

        while (dataInicio.compara(dataFim) != 0) {
            dataInicio = dataInicio.proximaData();
            diferenca++;
        }

        return diferenca;
    }

    /**
     * Retorna a diferença em segundo com a data passada
     *
     * @param data  instância da classe Data
     *
     * @return  diferença em segundos
     *
     */
    public long diferencaEmSegundos(Data data) {
        int segundo2, minuto2, hora2, dia2, mes2, ano2;
        int segundo1, minuto1, hora1;
        int diferSegundo, diferMinuto, diferHora, diferDia, diferMes, diferAno;
        Horario horario2;
        long diferenca = 0;

        horario2 = data.getHorario();
        dia2 = data.getDia();
        mes2 = data.getMes();
        ano2 = data.getAno();

        if (horario2 != null) {
            segundo2 = horario2.getSegundo();
            minuto2 = horario2.getMinuto();
            hora2 = horario2.getHora();
        } else {
            segundo2 = 0;
            minuto2 = 0;
            hora2 = 0;
        }

        if (horario != null) {
            segundo1 = horario.getSegundo();
            minuto1 = horario.getMinuto();
            hora1 = horario.getHora();
        } else {
            segundo1 = 0;
            minuto1 = 0;
            hora1 = 0;
        }

        diferSegundo = segundo2 - segundo1;
        diferMinuto = minuto2 - minuto1;
        diferHora = hora2 - hora1;
        diferDia = dia2 - dia;
        diferMes = mes2 - mes;
        diferAno = ano2 - ano;
        diferenca = (diferSegundo
                     + 60
                       * (diferMinuto
                          + 60
                            * (diferHora
                               + 24
                                 * (diferDia
                                    + 30 * (diferMes + 12 * diferAno)))));

        return diferenca;
    }

    /**
     * Verifica se a data passada é domingo
     *
     * @para data  data a ser verificada
     *
     * @return  <i>true</i> caso seja domingo, <br>
     * <i>false</i> caso contrário
     *
     */
    public static boolean ehDomingo(Data data) {
        GregorianCalendar calendar = new GregorianCalendar();
        int diaSemana;
        boolean ehDomingo = false;

        // O mês de GregorianCalendar vai de 0 a 11
        calendar.set(data.getAno(), data.getMes() - 1, data.getDia());

        diaSemana = calendar.get(Calendar.DAY_OF_WEEK);

        if (diaSemana == Calendar.SUNDAY) {
            ehDomingo = true;
        }

        return ehDomingo;
    }

    /**
     * Verifica se é fim de semana
     *
     * @return  <i>true</i> caso seja fim de semana, <br>
     * <i>false</i> caso contrário
     *
     */
    public boolean ehFinalDeSemana() {
        Data data = null;

        try {
            data = new Data(dia, mes, ano);
        } catch (Exception e) {}

        return ehFinalDeSemana(data);
    }

    /**
     * Verifica se a data passada é fim de semana
     *
     * @para data  data a ser verificada
     *
     * @return  <i>true</i> caso seja fim de semana, <br>
     * <i>false</i> caso contrário
     *
     */
    public static boolean ehFinalDeSemana(Data data) {
        GregorianCalendar calendar = new GregorianCalendar();
        int diaDaSemana = 0;
        boolean retorno = false;

        calendar.set(data.getAno() - 1900, data.getMes(), data.getDia());

        diaDaSemana = calendar.get(Calendar.DAY_OF_WEEK);

        if (diaDaSemana == Calendar.SUNDAY
                || diaDaSemana == Calendar.SATURDAY) {
            retorno = true;
        }

        return retorno;
    }

    /**
     * Verifica se a data recebida é a data atual
     *
     * @param data  A data a ser comparada com a data corrente
     *
     * @return  <i>true</i> caso seja a data atual, <br>
     * <i>false</i> caso contrário
     *
     */
    public boolean ehHoje(Data data) {
        Data dataCorrente = new Data();
        boolean ehHoje;

        if (data.getDia() == dataCorrente.getDia()
                && data.getMes() == dataCorrente.getMes()
                && data.getAno() == dataCorrente.getAno()) {
            ehHoje = true;
        } else {
            ehHoje = false;
        }

        return ehHoje;
    }

    /**
     * Verifica se a data passada é o mesmo dia desta data
     *
     * @para data  data a ser verificada
     *
     * @return  <i>true</i> caso seja o mesmo dia, <br>
     * <i>false</i> caso contrário
     *
     */
    public boolean ehMesmoDia(Data data) {
        boolean resultado = false;

        if (dia == data.getDia() && mes == data.getMes()
                && ano == data.getAno()) {
            resultado = true;
        }

        return resultado;
    }

    /**
     * Retorna a representação string da data no formato indicado
     *
     * @param formato  formato de retorno da data
     *
     * @return  representação string da data
     *
     */
    public String format(int formato) {
        String data = null;

        try {
            data = format(this, formato);
        } catch (DataInvalidaException e) {
            data = "";
        }

        return data;
    }

    /**
     * Retorna a representação string da data no formato indicado
     *
     * @param data     data a ser formatada
     * @param formato  formato de retorno da data
     *
     * @return  represetação string da data
     *
     * throws DataInvalidaException  exceção levantada caso algum dos valores
     * seja inválido
     *
     */
    public static String format(Data data,
                                int formato) throws DataInvalidaException {
        String segundoStr = "";
        String minutoStr = "";
        String horaStr = "";
        String diaStr = "";
        String mesStr = "";
        String anoStr = "";
        String texto = "";
        Horario horario;

        try {
            horario = data.getHorario();
            diaStr = String.valueOf(data.getDia());
            mesStr = String.valueOf(data.getMes());
            anoStr = String.valueOf(data.getAno());

            if (diaStr.length() < 2) {
                diaStr = "0" + diaStr;
            }

            if (mesStr.length() < 2) {
                mesStr = "0" + mesStr;
            }

            for (int i = anoStr.length(); i < 4; i++) {
                anoStr = "0" + anoStr;
            }

            if (horario != null) {
                segundoStr = String.valueOf(horario.getSegundo());
                minutoStr = String.valueOf(horario.getMinuto());
                horaStr = String.valueOf(horario.getHora());
            }

            switch (formato) {

            case (FORMATO1):
                texto = diaStr + "/" + mesStr + "/" + anoStr;

                break;

            case (FORMATO2):
                texto = diaStr + "/" + mesStr + "/" + anoStr;
                texto += ":" + horario.format(Horario.FORMATO1);

                break;

            case (FORMATO3):
                texto = diaStr + mesStr + anoStr;

                break;

            case (FORMATO4):
                texto = diaStr + mesStr + anoStr;
                texto += horario.format(Horario.FORMATO2);

                break;

            case (FORMATO5):
                texto = diaStr + "/" + mesStr + "/" + anoStr;

                break;

            default:
                texto = null;

                break;
            }
        } catch (NumberFormatException e) {
            throw new DataInvalidaException(e, data);
        }

        return texto;
    }

    /**
     * Retorna o ano
     *
     * @return  o ano
     *
     */
    public int getAno() {
        return ano;
    }

    /**
     * Retorna o dia
     *
     * @return  o dia
     *
     */
    public int getDia() {
        return dia;
    }

    /**
     * Retorna o Horário
     *
     * @return  uma instância da classe Horario
     *
     */
    public Horario getHorario() {
        return horario;
    }

    /**
     * Retorna o mês
     *
     * @return  o mês
     *
     */
    public int getMes() {
        return mes;
    }

    /**
     * Retorna o número de dias do mês.
     *
     * @param mes  mês
     *
     * @return  número de dias do mês
     */
    private int numeroDeDiasDoMes(int mes) {
        int retorno = -1;
        GregorianCalendar calendar = new GregorianCalendar();

        switch (mes) {

        case 1:
            retorno = 31;

            break;

        case 2:
            if (calendar.isLeapYear(ano)) {
                retorno = 29;
            } else {
                retorno = 28;
            }

            break;

        case 3:
            retorno = 31;

            break;

        case 4:
            retorno = 30;

            break;

        case 5:
            retorno = 31;

            break;

        case 6:
            retorno = 30;

            break;

        case 7:
            retorno = 31;

            break;

        case 8:
            retorno = 31;

            break;

        case 9:
            retorno = 30;

            break;

        case 10:
            retorno = 31;

            break;

        case 11:
            retorno = 30;

            break;

        case 12:
            retorno = 31;
        }

        return retorno;
    }

    /**
     * Retorna a data do dia seguinte
     *
     * @return  data do dia seguinte
     *
     */
    public Data proximaData() {
        Data dataRetorno = null;
        int diaProximaData;
        int mesProximaData;
        int anoProximaData;

        if (dia < this.numeroDeDiasDoMes(mes)) {
            diaProximaData = dia + 1;
            mesProximaData = mes;
            anoProximaData = ano;
        } else if (dia == this.numeroDeDiasDoMes(mes) && (mes != 12)) {
            diaProximaData = 1;
            mesProximaData = mes + 1;
            anoProximaData = ano;
        } else {
            diaProximaData = 1;
            mesProximaData = 1;
            anoProximaData = ano + 1;
        }

        try {
            dataRetorno = new Data(diaProximaData, mesProximaData,
                                   anoProximaData);
        } catch (DataInvalidaException e) {}

        return dataRetorno;
    }

    /**
     * Transforma string em data assumindo '/' como o separador utilizado.
     *
     * @param dataStr  data em forma de texto
     * @param formato  formato em que está a data
     *
     * @return  a data como uma instância da classe Data
     *
     * throws DataInvalidaException  exceção levantada caso algum dos valores
     * seja inválido
     *
     */
    public static Data stringToData(String dataStr,
                                    int formato) throws DataInvalidaException {
        int dia = 0;
        int mes = 0;
        int ano = 0;
        String diaStr, mesStr, anoStr;
        String minutoStr, segundoStr, horaStr;
        Data data = null;

        try {
            switch (formato) {

            case (FORMATO1):
                diaStr = dataStr.substring(0, 2);
                mesStr = dataStr.substring(3, 5);
                anoStr = dataStr.substring(6, 10);
                data = new Data(diaStr, mesStr, anoStr);

                break;

            case (FORMATO2):
                diaStr = dataStr.substring(0, 2);
                mesStr = dataStr.substring(3, 5);
                anoStr = dataStr.substring(6, 10);
                horaStr = dataStr.substring(11, 13);
                minutoStr = dataStr.substring(14, 16);
                segundoStr = dataStr.substring(17, 19);
                data = new Data(segundoStr, minutoStr, horaStr, diaStr, mesStr,
                                anoStr);

                break;

            case (FORMATO3):
                diaStr = dataStr.substring(0, 2);
                mesStr = dataStr.substring(2, 4);
                anoStr = dataStr.substring(4, 8);

                break;

            case (FORMATO4):
                diaStr = dataStr.substring(0, 2);
                mesStr = dataStr.substring(2, 4);
                anoStr = dataStr.substring(4, 8);
                horaStr = dataStr.substring(8, 10);
                minutoStr = dataStr.substring(10, 12);
                segundoStr = dataStr.substring(12, 14);
                data = new Data(segundoStr, minutoStr, horaStr, diaStr, mesStr,
                                anoStr);

                break;

            case (FORMATO5):
                anoStr = dataStr.substring(0, 4);
                mesStr = dataStr.substring(5, 7);
                diaStr = dataStr.substring(8, 10);
                data = new Data(diaStr, mesStr, anoStr);

                break;

            default:
                data = null;

                break;
            }
        } catch (Exception e) {
            throw new DataInvalidaException(dataStr);
        }

        return data;
    }

    /**
     * Retira um determinado número de dias da data
     *
     * @param data  data a ser retirado o número de dias
     * @param dias  número de dias a retirar
     *
     */
    public static void subDias(Data data, int dias) {
        Data novaData;

        novaData = data;

        while (dias > 0) {
            novaData = novaData.dataAnterior();
            dias = dias - 1;
        }

        data.dia = novaData.dia;
        data.mes = novaData.mes;
        data.ano = novaData.ano;
        data.horario = novaData.horario;
    }

    /**
     * Retorna a data do último domingo
     *
     * @return  A data correspondente ao último domingo
     *
     */
    public Data ultimoDomingo() {
        Data data = new Data();

        while (!ehDomingo(data)) {
            data = dataAnterior(data);
        }

        return data;
    }

    /**
     * Valida uma data (dia, mes e ano)
     *
     * @param dia      dia
     * @param mes      mês
     * @param ano      ano
     *
     * throws DataInvalidaException  exceção levantada caso algum dos valores
     * seja invalido
     *
     */
    private void validaData(int dia, int mes,
                            int ano) throws DataInvalidaException {
        if (!((0 < mes) && (mes <= 12) && (dia > 0)
                && (dia <= numeroDeDiasDoMes(mes)))) {
            throw new DataInvalidaException(dia, mes, ano);
        }
    }

    /**
     * Retorna a data no formato DD/MM/AAAA, incluindo as barras (string)
     *
     * @return  A data no formato string
     *
     */
    public String getDataAtual() {

        String dia;
        String mes;
        String ano;
        String dataAtual = "";
        GregorianCalendar calendar = new GregorianCalendar();

        dia = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
        mes = String.valueOf(calendar.get(Calendar.MONTH)+1);
        ano = String.valueOf(calendar.get(Calendar.YEAR));

        if (dia.length() < 2) {
            dia = "0" + dia;
        }

        if (mes.length() < 2) {
            mes = "0" + mes;
        }

        for (int i = ano.length(); i < 4; i++) {
            ano = "0" + ano;
        }

        dataAtual = dia + "/" + mes + "/" + ano;

        return dataAtual;
    }

    /**
     * Retorna o horario no formato HH:MM, incluindo os dois pontos (:)
     *
     * @return  O horário no formato string
     *
     */

    public String getHorarioAtual() {
        String hora;
        String minuto;
        String horaAtual;
        Horario horario = new Horario();

        hora = String.valueOf(horario.getHora());
        minuto = String.valueOf(horario.getMinuto());

        if (hora.length() < 2) {
            hora = "0" + hora;
        }


        if (minuto.length() < 2) {
            minuto = "0" + minuto;
        }

        horaAtual = hora + ":" + minuto;

        return horaAtual;
    }

    /**
     * Monta uma string com os atributos de Data, que será utilizado quando a
     * data for impressa.
     *
     * @return        String com os dados da data
     *
     * @since         Criado em 16/08/2001
     *
     */
    public String toString() {
        try {
            return format(this,1);
        } catch(Exception e) {
            return "";
        }
    }

}



/*--- formatting done in "CESAR" style on 09-07-2000 ---*/

