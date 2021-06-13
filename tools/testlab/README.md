# Test Lab

## Host preparation

To be able to access the services with their FQDN add aliases to the `/etc/hosts`. 
Note that this is a hack rather than a proper solution.

```
/etc/hosts:
127.0.0.100     kerby.example.com
127.0.0.101     openldap.example.com
127.0.0.102     fedora389ds.example.com
```

## Apache Kerby as Kerberos KDC

Uses Docker image `coheigea/kerby`, source at https://github.com/coheigea/testcases/tree/master/apache/docker/kerby.

Start the Kerby server

```
docker run -it --rm --name kerby -h kerby.example.com -u $(id -u):$(id -g) -p 60088:60088/tcp -p 60088:60088/udp -v $(pwd)/tools/testlab/kerby-data:/kerby-data coheigea/kerby
```

Initial creation of service accounts and keytabs:

```
docker exec -it kerby bash
stty rows 24 columns 80
sh bin/kadmin.sh /kerby-data/conf/ -k /kerby-data/keytabs/admin.keytab

addprinc -pw secret hnelson@EXAMPLE.COM

addprinc -pw randall ldap/openldap.example.com@EXAMPLE.COM
ktadd -k /kerby-data/keytabs/ldap.keytab ldap/openldap.example.com@EXAMPLE.COM
addprinc -pw randall ldap/fedora389ds.example.com@EXAMPLE.COM
ktadd -k /kerby-data/keytabs/ldap.keytab ldap/fedora389ds.example.com@EXAMPLE.COM
```

## OpenLDAP

```
docker run -it --rm --name openldap -h openldap.example.com -p 20389:389 -p 20636:636 -e LDAP_TLS_VERIFY_CLIENT=never -v $(pwd)/tools/testlab/ldap.keytab:/etc/krb5.keytab -v $(pwd)/tools/testlab/krb5.conf:/etc/krb5.conf osixia/openldap:1.5.0
```

## Fedora 389ds

```
docker run -it --rm --name fedora389ds -h fedora389ds.example.com -p 21389:3389 -p 21636:3636 -e DS_DM_PASSWORD=admin -v $(pwd)/tools/testlab/ldap.keytab:/etc/krb5.keytab -v $(pwd)/tools/testlab/krb5.conf:/etc/krb5.conf 389ds/dirsrv bash -c "zypper install -y cyrus-sasl-crammd5 cyrus-sasl-digestmd5 cyrus-sasl-gssapi; set -m; /usr/lib/dirsrv/dscontainer -r & while ! /usr/lib/dirsrv/dscontainer -H; do sleep 5; done; sleep 5; /usr/sbin/dsconf localhost backend create --suffix dc=example,dc=org --be-name example; fg"
```

## Usage

### GSSAPI authentication

```
export KRB5_CONFIG=$(pwd)/tools/testlab/krb5.conf
echo "secret" | kinit hnelson
ldapwhoami -H ldap://openldap.example.com:20389 -Y GSSAPI -N
ldapwhoami -H ldap://fedora389ds.example.com:21389 -Y GSSAPI -N
```

### UI integration tests

```
docker run -it --rm \
    -u $(id -u):$(id -g) \
    -v ~/.m2:/home/hnelson/.m2 \
    -v $(pwd):/home/hnelson/project \
    -v $(pwd)/tools/testlab/krb5.conf:/etc/krb5.conf \
    --link=kerby:kerby.example.com \
    --link=openldap:openldap.example.com -e OPENLDAP_HOST=openldap.example.com -e OPENLDAP_PORT=389 -e OPENLDAP_PORT_SSL=636 \
    --link=fedora389ds:fedora389ds.example.com -e FEDORA_389DS_HOST=fedora389ds.example.com -e FEDORA_389DS_PORT=3389 -e FEDORA_389DS_PORT_SSL=3636 \
    apachedirectory/maven-build:jdk-11 bash -c "mvn -V -f pom-first.xml clean install && mvn -V clean install -Denable-ui-tests"

```

