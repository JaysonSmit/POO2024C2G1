package pe.edu.upeu.calcfx.modelo;

public class CalcTO {
    private String val1;
    private String val2;
    private char operador;
    private String resultado;

    // Getters y Setters
    public String getVal1() {
        return val1;
    }

    public void setVal1(String val1) {
        this.val1 = val1;
    }

    public String getVal2() {
        return val2;
    }

    public void setVal2(String val2) {
        this.val2 = val2;
    }

    public char getOperador() {
        return operador;
    }

    public void setOperador(char operador) {
        this.operador = operador;
    }

    public String getResultado() {
        return resultado;
    }

    public void setResultado(String resultado) {
        this.resultado = resultado;
    }

    @Override
    public String toString() {
        return "CalcTO{" +
                "val1='" + val1 + '\'' +
                ", val2='" + val2 + '\'' +
                ", operador=" + operador +
                ", resultado='" + resultado + '\'' +
                '}';
    }
}
