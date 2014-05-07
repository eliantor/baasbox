import static play.mvc.Http.Status.OK;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.routeAndCall;
import static play.test.Helpers.running;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Test;

import play.mvc.Result;
import play.test.FakeRequest;
import core.AbstractRootTest;
import core.TestConfig;


public class RootResetAdminPasswordTest extends AbstractRootTest{

	
	@Test 
	public void test()
	{
		running
		(
			getFakeApplication(), 
			new Runnable() 
			{
				public void run() 
				{
					FakeRequest request = new FakeRequest(getMethod(), getRouteAddress());
					request = request.withHeader(TestConfig.KEY_APPCODE, TestConfig.VALUE_APPCODE);
					request = request.withHeader(TestConfig.KEY_AUTH, TestConfig.AUTH_ROOT_ENC);
					try {
						request = request.withJsonBody(new ObjectMapper().readTree("{\"password\":\"123\"}"));
					} catch (JsonProcessingException e) {
						throw new RuntimeException(e);
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
					Result result = routeAndCall(request);
					assertRoute(result, "changeAdminPassword", OK, null, false);
					
					//check the changed pwd
					request = new FakeRequest("GET", "/me");
					request = request.withHeader(TestConfig.KEY_APPCODE, TestConfig.VALUE_APPCODE);
					request = request.withHeader(TestConfig.KEY_AUTH, TestConfig.AUTH_ADMIN_ENC);
					result = routeAndCall(request);
					assertRoute(result, "checkOldPass", 401, null, false);
					
					//check the new one
					request = new FakeRequest("GET", "/me");
					request = request.withHeader(TestConfig.KEY_APPCODE, TestConfig.VALUE_APPCODE);
					request = request.withHeader(TestConfig.KEY_AUTH, TestConfig.encodeAuth("admin", "123"));
					result = routeAndCall(request);
					assertRoute(result, "checkNewPass", 200, null, false);
					
					//reset the admin pass
					request = new FakeRequest(getMethod(), getRouteAddress());
					request = request.withHeader(TestConfig.KEY_APPCODE, TestConfig.VALUE_APPCODE);
					request = request.withHeader(TestConfig.KEY_AUTH, TestConfig.AUTH_ROOT_ENC);
					try {
						request = request.withJsonBody(new ObjectMapper().readTree("{\"password\":\""+TestConfig.AUTH_ADMIN_PASS+"\"}"));
					} catch (JsonProcessingException e) {
						throw new RuntimeException(e);
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
					result = routeAndCall(request);
					assertRoute(result, "resetAdminPassword", OK, null, false);
					
					//check the old password
					//check the changed pwd
					request = new FakeRequest("GET", "/me");
					request = request.withHeader(TestConfig.KEY_APPCODE, TestConfig.VALUE_APPCODE);
					request = request.withHeader(TestConfig.KEY_AUTH, TestConfig.AUTH_ADMIN_ENC);
					result = routeAndCall(request);
					assertRoute(result, "checkOldPass2", OK, null, false);
					
					
				}
			}
		);
	}

	@After
	public void resetAdminPass(){
		
	}
	
	@Override
	public String getRouteAddress() {
		return  "/root/resetadminpassword";
	}

	@Override
	public String getMethod() {
		return "POST";
	}

	@Override
	protected void assertContent(String s) {
		// TODO Auto-generated method stub
		
	}
}
