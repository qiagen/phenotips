appname_file=phenotips
installdir=phenotips
livelink=phenotips-live
start="/usr/local/ingenuity/phenotips/phenotips-live/bin/phenotips.sh start"
stop="/usr/local/ingenuity/phenotips/phenotips-live/bin/phenotips.sh stop"
stop_to_install=yes
force_stop=
propertyfiles=web/WEB-INF/xwiki.cfg,web/WEB-INF/classes/xwiki.properties,setup/setup.properties,setup/phenotipsbuild-config.properties
keepoldcopies=5
setup=setup/deployment.phenotips.setup
tomcathttpport=8144
tomcatstopport=8044

Version=1.0
cfmap_monitor_enabled=true
cfmap_register_port=8144
cfmap_register_version=
cfmap_register_z=dev
cfmap_register_url="http://`hostname | cut -d'.' -f1`.ingenuity.com:8144/phenotips"
cfmap_monitor_type_resource="http://`hostname | cut -d'.' -f1`.ingenuity.com:8144//phenotips"
cfmap_monitor_frequency=5
cfmap_monitor_type=http
~                                      
