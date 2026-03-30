export type OpenClawInboundMessage = {
  type: "message";
  msgId: string;
  from_user_id: string;
  content: string;
};

export type OpenClawOutboundReply = {
  type: "reply";
  msgId: string;
  to_user_id: string;
  content: string;
};

export type JavaChatRequest = {
  userId: string;
  message: string;
};

export type JavaChatResponse = {
  reply: string;
};

