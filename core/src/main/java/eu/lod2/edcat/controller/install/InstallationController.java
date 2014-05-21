package eu.lod2.edcat.controller.install;

import eu.lod2.edcat.model.Installation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

/**
 * The InstallationController aides you in the installation of a new DCAT and in adding
 * new Catalogs to this DCAT deployment.
 */
@Controller
public class InstallationController {

  /**
   * Sets up a new installation of the DCAT store.
   *
   * @param request No special parameters are expected
   * @return HttpStatus.CREATED on success.
   * @throws Throwable May throw any form of error upon failure.
   */
  // @SuppressWarnings( "UnusedDeclaration" )
  @RequestMapping(value = "install", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
  public ResponseEntity<Object> create(HttpServletRequest request) throws Throwable {
    Installation.install();
    return new ResponseEntity<Object>( HttpStatus.CREATED );
  }

}
