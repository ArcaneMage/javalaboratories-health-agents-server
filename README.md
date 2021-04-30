# Health-Agents Server

## Technologies
* Spring Boot 2.4.4, Spring Framework 5.3.5
* Digest Authorization security applied to REST API
* RsaSecureIdAuthenticationFilter introduced for additional security of REST API  
* Enabled HTTPS/SSL

## Introduction
This application has a number of implemented `Health-Probe` agents that are
responsible or detecting the availability of specific services, these are as follows:
| HealthProbe       | Description                           | Endpoint                         |
| ------------------|---------------------------------------|----------------------------------|
| OpenVpnHealthProbe| Detects availability of OpenVPN server| /api/agents/secure-traffic/health|
| PureFtpHealthProbe| Detects availability of PureFTP server| /api/agents/ftp/health           |

There will be additional `HealthProbes` implemented in the not too distant future. The inspiration for
this application came about when I deployed the `OpenVPN` server in the cloud, but needed to determine
its stability and availability via the `UptimeRobot` service. This service sends requests to this application
at regular intervals and if the HTTP response is anything other than 200, it would immediately raise
and alert.

Below having executed the `/api/agents/secure-traffic/health` endpoint, the following response
indicates the VPN server is down, hence the 501 status code.
```
{
    "agent": "VPN-Probe",
    "status": 501,
    "meaning": "Not Implemented",
    "message": "Probed service with 'VPN-Probe' and it appears to be down",
    "timestamp": "2021-04-30 04:01:49 (BST)"
}
```
However, having executed the `/api/agents/secure-traffic/health` endpoint, after the issue has been 
rectified, the following response indicates the VPN server is now rectified, hence the 200 status code.
```
{
    "agent": "VPN-Probe",
    "status": 200,
    "meaning": "OK",
    "message": "No additional information",
    "timestamp": "2021-04-30 05:00:49 (BST)"
}
```
## REST API Endpoints
Endpoints are secured with Digest security roles, namely the `ROLE_MONITOR`, and the `monitor` user
has this role. Having said that, the `test` user has `ROLE_NONE` assigned and therefore has no access
to any of the endpoints. Therefore, if the incorrect credentials are supplied, 401 (Unauthorised) or 
403 (Forbidden) is returned. Because the credentials are encrypted with MD5, and sent over HTTPS, this 
makes access control extremely secure.

## Building the application
1. Clone the repository
2. Enter the following command `mvn clean package` in the base directory of the project (where the 
   pom.xml exists)
3. Alternatively, for deployment in a Linux environment, consider the following command `mvn clean 
   verify` and this will create a zipped package, something like `health-agents-server-1.0.3-SNAPSHOT.zip`,
   containing `bash-scripts` to manage the server. 

# LICENSE
Version 2.0, (c) 2021 javalaboratories.org, Kevin Henry (AM)
