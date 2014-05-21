package eu.lod2.edcat.controller;

import eu.lod2.edcat.model.ErrorResponse;
import eu.lod2.edcat.utils.NotFoundException;
import eu.lod2.hooks.handlers.dcat.ActionAbortException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;

public abstract class BaseController {
  /** Logging aid */
  protected Logger logger = LoggerFactory.getLogger(this.getClass());

  @ExceptionHandler(ActionAbortException.class)
  public ResponseEntity handleActionAbort( HttpServletRequest req, ActionAbortException exception ) {
    logger.warn("Request: " + req.getRequestURL() + " aborted by " + exception.getMessage());
    return new ResponseEntity<Object>( new ErrorResponse("action aborted: " + exception.getMessage(),exception.getStatus().name()),exception.getStatus());
  }

  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity handleNotFound( HttpServletRequest req, NotFoundException exception ) {
    logger.info("Request: " + req.getRequestURL() + " resulted in not found exception");
    return new ResponseEntity<Object>( new ErrorResponse(req.getRequestURI() + " not found", HttpStatus.NOT_FOUND.name()), HttpStatus.NOT_FOUND );
  }
}
