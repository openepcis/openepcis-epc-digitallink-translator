package io.openepcis.qrcode.generator;

import io.quarkus.vertx.web.Route;
import io.vertx.ext.web.RoutingContext;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.info.License;

@OpenAPIDefinition(info =
@Info(
        title = "OpenEPCIS QR Code Generator API",
        description = "Generate QR code with various config for GS1 Digital Link WebURI Application Identifiers",
        version = "0.9.1",
        license =
        @License(name = "Apache 2.0", url = "http://www.apache.org/licenses/LICENSE-2.0")
)
)
@ApplicationPath("/")
public class RESTApplication extends Application {
    @Route(path = "/", methods = Route.HttpMethod.GET)
    @Operation(hidden = true)
    void baseUrl(final RoutingContext rc) {
        rc.redirect("q/swagger-ui/");
    }
}
