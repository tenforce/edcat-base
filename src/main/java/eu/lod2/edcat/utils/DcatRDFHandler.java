package eu.lod2.edcat.utils;

import org.openrdf.model.Statement;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.helpers.RDFHandlerBase;

import java.util.ArrayList;
import java.util.List;

public class DcatRDFHandler extends RDFHandlerBase {
  private List<Statement> statements= new ArrayList<Statement>(10);

  @Override
  public void handleStatement(Statement st) throws RDFHandlerException
  {
    statements.add(st);
  }

  public List<Statement> getStatements() {
    return statements;
  }
}
