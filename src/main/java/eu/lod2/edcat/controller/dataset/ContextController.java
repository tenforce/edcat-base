package eu.lod2.edcat.controller.dataset;

import eu.lod2.edcat.utils.JsonLdContext;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;

@Controller
public class ContextController {

  // GET /context.jsonld
  @RequestMapping(value = "contexts/{kind}.jsonld", method = RequestMethod.GET)
  @ResponseBody
  public void getContext( HttpServletResponse response , String kind ) throws Throwable {
    InputStream contextIn = JsonLdContext.getContextLocation( kind ).openStream();
    String contextString = IOUtils.toString( contextIn, "UTF-8" );
    contextIn.close();
    response.setStatus( 200 );
    response.setContentType( "application/json" );
    response.getWriter().write( contextString );
  }
}
