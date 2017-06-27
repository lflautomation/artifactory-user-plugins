import spock.lang.Specification
import org.jfrog.artifactory.client.model.repository.settings.impl.MavenRepositorySettingsImpl
import static org.jfrog.artifactory.client.ArtifactoryClient.create
import groovyx.net.http.HttpResponseException

class PgpVerifyTest extends Specification {
    def 'pgp verify test'() {
        setup:
        def baseurl = 'http://localhost:8088/artifactory'
        def artifactory = create(baseurl, 'admin', 'password')

        def builder = artifactory.repositories().builders()
        def local = builder.localRepositoryBuilder().key('maven-local')
        .repositorySettings(new MavenRepositorySettingsImpl()).build()
        artifactory.repositories().create(0, local)

        def xmlfile = new File('./src/test/groovy/PgpVerifyTest/maven-metadata.xml')
        def ascfile = new File('./src/test/groovy/PgpVerifyTest/maven-metadata.xml.asc')

        artifactory.repository('maven-local').upload('maven-metadata.xml', xmlfile).doUpload()
        artifactory.repository('maven-local').upload('maven-metadata.xml.asc', ascfile).doUpload()

        when:
        artifactory.repository('maven-local').download('maven-metadata.xml').doDownload()

        then:
        notThrown(HttpResponseException)

        cleanup:
        artifactory.repository('maven-local').delete()
    }
}
