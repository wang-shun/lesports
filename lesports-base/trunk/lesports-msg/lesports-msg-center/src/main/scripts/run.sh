#!/bin/bash

SCRIPT_HOME=$(dirname $(readlink -f $0))
bash -ex $SCRIPT_HOME/server_ctl.sh restart -p 9625 -m MsgServer