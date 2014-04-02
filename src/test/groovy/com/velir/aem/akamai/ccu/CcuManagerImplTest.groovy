package com.velir.aem.akamai.ccu

import com.github.tomakehurst.wiremock.WireMockServer
import org.osgi.service.component.ComponentContext
import spock.lang.Specification

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig

/**
 * CcuManagerImplTest -
 *
 * @author Sebastien Bernard
 */
class CcuManagerImplTest extends Specification {
	private static WireMockServer wireMockServer;
	private static CcuManagerImpl ccuManager = new CcuManagerImpl();

	def setupSpec() {
		wireMockServer = new WireMockServer(wireMockConfig().port(4444))
		wireMockServer.start()

		ComponentContext context = Mock(ComponentContext.class)
		context.getProperties() >> new Hashtable([rootCcuUrl: "http://localhost:4444", userName: "test", password: "test", defaultPurgeDomain: "staging"])
		ccuManager.activate(context);
	}

	def "PurgeByUrl"() {
		when:
		def response = ccuManager.purgeByUrl("http://test")

		then:
		response.httpStatus == 201
		response.detail == "Request accepted."
		response.estimatedSeconds == 420
		response.purgeId == "95b5a092-043f-4af0-843f-aaf0043faaf0"
		response.progressUri == "/ccu/v2/purges/95b5a092-043f-4af0-843f-aaf0043faaf0"
		response.pingAfterSeconds == 420
		response.supportId == "17PY1321286429616716-211907680"
	}

	def "PurgeByUrls"() {
		when:
		def response = ccuManager.purgeByUrls(["http://test", "http://test2"])

		then:
		response.httpStatus == 201
		response.detail == "Request accepted."
		response.estimatedSeconds == 420
		response.purgeId == "95b5a092-043f-4af0-843f-aaf0043faaf0"
		response.progressUri == "/ccu/v2/purges/95b5a092-043f-4af0-843f-aaf0043faaf0"
		response.pingAfterSeconds == 420
		response.supportId == "17PY1321286429616716-211907680"
	}

	def "PurgeByCpCode"() {
		when:
		def response = ccuManager.purgeByCpCode("123456")

		then:
		response.httpStatus == 201
		response.detail == "Request accepted."
		response.estimatedSeconds == 420
		response.purgeId == "95b5a092-043f-4af0-843f-aaf0043faaf0"
		response.progressUri == "/ccu/v2/purges/95b5a092-043f-4af0-843f-aaf0043faaf0"
		response.pingAfterSeconds == 420
		response.supportId == "17PY1321286429616716-211907680"
	}

	def "PurgeByCpCodes"() {
		when:
		def response = ccuManager.purgeByCpCodes(["123456", "789456"])

		then:
		response.httpStatus == 201
		response.detail == "Request accepted."
		response.estimatedSeconds == 420
		response.purgeId == "95b5a092-043f-4af0-843f-aaf0043faaf0"
		response.progressUri == "/ccu/v2/purges/95b5a092-043f-4af0-843f-aaf0043faaf0"
		response.pingAfterSeconds == 420
		response.supportId == "17PY1321286429616716-211907680"
	}

	def "Purge"() {
		when:
		def response = ccuManager.purge(["123456", "789456"], PurgeType.CPCODE, PurgeAction.INVALIDATE, PurgeDomain.PRODUCTION)

		then:
		response.httpStatus == 201
		response.detail == "Request accepted."
		response.estimatedSeconds == 420
		response.purgeId == "95b5a092-043f-4af0-843f-aaf0043faaf0"
		response.progressUri == "/ccu/v2/purges/95b5a092-043f-4af0-843f-aaf0043faaf0"
		response.pingAfterSeconds == 420
		response.supportId == "17PY1321286429616716-211907680"
	}

	def "GetPurgeStatus"() {
		when:
		def status = ccuManager.getPurgeStatus("/ccu/v2/purges/142eac1d-99ab-11e3-945a-7784545a7784")

		then:
		status.originalEstimatedSeconds == 480
		status.progressUri == "/ccu/v2/purges/142eac1d-99ab-11e3-945a-7784545a7784"
		status.originalQueueLength == 6
		status.purgeId == "142eac1d-99ab-11e3-945a-7784545a7784"
		status.supportId == "17SY1392844709041263-238396512"
		status.httpStatus == 200
		status.completionTime == 10
		status.submittedBy == "test1"
		status.purgeStatus == "In-Progress"
		status.submissionTime == "2014-02-19T21:16:20Z"
		status.pingAfterSeconds == 60
	}

	def "GetQueueStatus"() {
		when:
		def status = ccuManager.queueStatus

		then:
		status.httpStatus == 200
		status.detail == "The queue may take a minute to reflect new or removed requests."
		status.queueLength == 0
		status.supportId == "17QY1396454277968590-306197600"
	}

	def cleanupSpec() {
		ccuManager.deactivate()
		wireMockServer.stop()
	}
}
