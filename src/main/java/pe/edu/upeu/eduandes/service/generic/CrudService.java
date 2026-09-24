package pe.edu.upeu.eduandes.service.generic;

import java.util.List;

public interface CrudService <REQ, RES, ID>{
    RES create(REQ t);
    RES update(ID id, REQ t);
    RES read(ID id);
    void delete(ID id);
    List<RES> readAll();
}
