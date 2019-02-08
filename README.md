# OpenMBEE Model Persistent Service Provider Interface (MPSPI)

OpenMBEE Model Persistent Service Provider Interface (SPI) provides a simple and uniform interface to create, read, delete, load, save, and commit EMF models. It may work on any EObject and can provide pluggable adapter services.
Service providers implementing this SPI are called "adapters."   Each adapter accesses various tools including UML/SysML modeling, requirement management, simulation modeling, etc, and service consumers using this SPI (e.g. MapleMBSE) can edit models via such adapters.

## License
This project is developed and distributed under Apache 2.0 or Eclipse Public License (EPL) 2.0.  If you use any results of this project, you can choose one of these licenses.
