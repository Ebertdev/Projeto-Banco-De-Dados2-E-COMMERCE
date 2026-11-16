package br.edu.bd2.loja.api;

import java.util.Collections;
import javax.persistence.PersistenceException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ErroMapper implements ExceptionMapper<PersistenceException> {

    @Override
    public Response toResponse(PersistenceException e) {
        Throwable causa = e;
        while (causa.getCause() != null) {
            causa = causa.getCause();
        }
        return Response.status(Response.Status.CONFLICT)
                .type(MediaType.APPLICATION_JSON)
                .entity(Collections.singletonMap("erro", "Violação de integridade: " + causa.getMessage()))
                .build();
    }
}
