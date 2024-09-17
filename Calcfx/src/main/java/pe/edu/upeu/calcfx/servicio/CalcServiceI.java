package pe.edu.upeu.calcfx.servicio;

import pe.edu.upeu.calcfx.modelo.CalcTO;

import java.util.List;

public interface CalcServiceI {
    public void guardarResultados(CalcTO to);
    public List<CalcTO> obtenerResultados ();
    public void eliminarResultados(int index);
    public void actualizarResultados (CalcTO to,int index);

}
