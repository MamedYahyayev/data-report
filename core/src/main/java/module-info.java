module azscrape.core {
  requires org.apache.logging.log4j;
  requires com.google.common;
  requires org.jetbrains.annotations;
  requires commons.validator;

  exports az.caspian.core;
  exports az.caspian.core.tree;
  exports az.caspian.core.utils;
  exports az.caspian.core.messaging;
  exports az.caspian.core.model;
  exports az.caspian.core.model.enumeration;
  exports az.caspian.core.constant;
  exports az.caspian.core.serialization;
  exports az.caspian.core.service;
  exports az.caspian.core.remote;
  exports az.caspian.core.io;
  exports az.caspian.core.template;
  exports az.caspian.core.task;
  exports az.caspian.core.tree.node;
  exports az.caspian.core.retry;
}
