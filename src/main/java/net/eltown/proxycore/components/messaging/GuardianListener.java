package net.eltown.proxycore.components.messaging;

import lombok.SneakyThrows;
import net.eltown.proxycore.ProxyCore;
import net.eltown.proxycore.components.data.guardian.GuardianCalls;
import net.eltown.proxycore.components.data.guardian.PunishmentLogDocument;
import net.eltown.proxycore.components.tinyrabbit.TinyRabbitListener;

import java.util.LinkedList;

public class GuardianListener {

    private final ProxyCore proxyCore;
    private final TinyRabbitListener listener;

    public GuardianListener(final ProxyCore instance) {
        this.proxyCore = instance;
        this.listener = new TinyRabbitListener("localhost");
        this.listener.throwExceptions(true);

        this.startListening();
    }

    @SneakyThrows
    public void startListening() {
        final TinyRabbitListener listener = new TinyRabbitListener("localhost");
        listener.callback((request -> {
            switch (GuardianCalls.valueOf(request.getKey().toUpperCase())) {
                case REQUEST_INITIATE_BAN:
                    this.proxyCore.getBanHandler().isActiveBan(request.getData()[1], is -> {
                        if (is) {
                            request.answer(GuardianCalls.CALLBACK_BAN_IS_BANNED.name(), "null");
                        } else {
                            this.proxyCore.getBanHandler().initiateBan(request.getData()[1], request.getData()[2], request.getData()[3], Long.parseLong(request.getData()[4]), id -> {
                                request.answer(GuardianCalls.CALLBACK_BAN_EXECUTED.name(), id);
                            });
                        }
                    });
                    break;
                case REQUEST_INITIATE_MUTE:
                    this.proxyCore.getMuteHandler().isActiveMute(request.getData()[1], is -> {
                        if (is) {
                            request.answer(GuardianCalls.CALLBACK_MUTE_IS_MUTED.name(), "null");
                        } else {
                            this.proxyCore.getMuteHandler().initiateMute(request.getData()[1], request.getData()[2], request.getData()[3], Long.parseLong(request.getData()[4]), id -> {
                                request.answer(GuardianCalls.CALLBACK_MUTE_EXECUTED.name(), id);
                            });
                        }
                    });
                    break;
                case REQUEST_GET_BAN_BEHAVIOR_ENTRIES:
                    this.proxyCore.getBanHandler().getBanBehaviorEntries(request.getData()[1], "BL", punishmentLogDocuments -> {
                        if (punishmentLogDocuments.size() == 0) {
                            request.answer(GuardianCalls.CALLBACK_ENTRIES_NOT_FOUND.name(), "null");
                        } else {
                            final LinkedList<String> list = new LinkedList<>();
                            for (PunishmentLogDocument e : punishmentLogDocuments) {
                                list.add(e.getId() + ">>" + e.getLogId() + ">>" + e.getTarget() + ">>" + e.getReason() + ">>" + e.getExecutor() + ">>" + e.getDate() + ">>" + e.getTimeEnd() + ">>" + e.getTimeStart());
                            }
                            request.answer(GuardianCalls.CALLBACK_BAN_BEHAVIOR_ENTRIES_RECEIVED.name(), list.toArray(String[]::new));
                        }
                    });
                    break;
                case REQUEST_GET_MUTE_BEHAVIOR_ENTRIES:
                    this.proxyCore.getMuteHandler().getMuteBehaviorEntries(request.getData()[1], "ML", punishmentLogDocuments -> {
                        if (punishmentLogDocuments.size() == 0) {
                            request.answer(GuardianCalls.CALLBACK_ENTRIES_NOT_FOUND.name(), "null");
                        } else {
                            final LinkedList<String> list = new LinkedList<>();
                            for (PunishmentLogDocument e : punishmentLogDocuments) {
                                list.add(e.getId() + ">>" + e.getLogId() + ">>" + e.getTarget() + ">>" + e.getReason() + ">>" + e.getExecutor() + ">>" + e.getDate() + ">>" + e.getTimeEnd() + ">>" + e.getTimeStart());
                            }
                            request.answer(GuardianCalls.CALLBACK_MUTE_BEHAVIOR_ENTRIES_RECEIVED.name(), list.toArray(String[]::new));
                        }
                    });
                    break;
                case REQUEST_CANCEL_BAN:
                    this.proxyCore.getBanHandler().isActiveBan(request.getData()[1], is -> {
                        if (!is) {
                            request.answer(GuardianCalls.CALLBACK_BAN_IS_NOT_BANNED.name(), "null");
                        } else {
                            this.proxyCore.getBanHandler().cancelBan(request.getData()[1], request.getData()[2], request.getData()[3], id -> {
                                request.answer(GuardianCalls.CALLBACK_BAN_CANCELLED.name(), id);
                            });
                        }
                    });
                    break;
                case REQUEST_CANCEL_MUTE:
                    this.proxyCore.getMuteHandler().isActiveMute(request.getData()[1], is -> {
                        if (!is) {
                            request.answer(GuardianCalls.CALLBACK_MUTE_IS_NOT_MUTED.name(), "null");
                        } else {
                            this.proxyCore.getMuteHandler().cancelMute(request.getData()[1], request.getData()[2], request.getData()[3], id -> {
                                request.answer(GuardianCalls.CALLBACK_MUTE_CANCELLED.name(), id);
                            });
                        }
                    });
                    break;
                case REQUEST_GET_BAN_ENTRY_TARGET:
                    this.proxyCore.getBanHandler().isActiveBan(request.getData()[1], is -> {
                        if (!is) {
                            request.answer(GuardianCalls.CALLBACK_BAN_IS_NOT_BANNED.name(), "null");
                        } else {
                            this.proxyCore.getBanHandler().getActiveBanEntryByTarget(request.getData()[1], e -> {
                                final LinkedList<String> list = new LinkedList<>();
                                list.add(e.getId() + ">>" + e.getTarget() + ">>" + e.getReason() + ">>" + e.getExecutor() + ">>" + e.getDate() + ">>" + e.getDuration());
                                request.answer(GuardianCalls.CALLBACK_ACTIVE_BAN_ENTRY_RECEIVED.name(), list.toArray(String[]::new));
                            });
                        }
                    });
                    break;
                case REQUEST_GET_MUTE_ENTRY_TARGET:
                    this.proxyCore.getMuteHandler().isActiveMute(request.getData()[1], is -> {
                        if (!is) {
                            request.answer(GuardianCalls.CALLBACK_MUTE_IS_NOT_MUTED.name(), "null");
                        } else {
                            this.proxyCore.getMuteHandler().getActiveMuteEntryByTarget(request.getData()[1], e -> {
                                final LinkedList<String> list = new LinkedList<>();
                                list.add(e.getId() + ">>" + e.getTarget() + ">>" + e.getReason() + ">>" + e.getExecutor() + ">>" + e.getDate() + ">>" + e.getDuration());
                                request.answer(GuardianCalls.CALLBACK_ACTIVE_MUTE_ENTRY_RECEIVED.name(), list.toArray(String[]::new));
                            });
                        }
                    });
                    break;
                case REQUEST_GET_BAN_ENTRY_ID:
                    this.proxyCore.getBanHandler().isActiveBanId(request.getData()[1], is -> {
                        if (!is) {
                            request.answer(GuardianCalls.CALLBACK_BAN_IS_NOT_BANNED.name(), "null");
                        } else {
                            this.proxyCore.getBanHandler().getActiveBanEntryById(request.getData()[1], e -> {
                                final LinkedList<String> list = new LinkedList<>();
                                list.add(e.getId() + ">>" + e.getTarget() + ">>" + e.getReason() + ">>" + e.getExecutor() + ">>" + e.getDate() + ">>" + e.getDuration());
                                request.answer(GuardianCalls.CALLBACK_ACTIVE_BAN_ENTRY_RECEIVED.name(), list.toArray(String[]::new));
                            });
                        }
                    });
                    break;
                case REQUEST_GET_MUTE_ENTRY_ID:
                    this.proxyCore.getMuteHandler().isActiveMuteId(request.getData()[1], is -> {
                        if (!is) {
                            request.answer(GuardianCalls.CALLBACK_MUTE_IS_NOT_MUTED.name(), "null");
                        } else {
                            this.proxyCore.getMuteHandler().getActiveMuteEntryById(request.getData()[1], e -> {
                                final LinkedList<String> list = new LinkedList<>();
                                list.add(e.getId() + ">>" + e.getTarget() + ">>" + e.getReason() + ">>" + e.getExecutor() + ">>" + e.getDate() + ">>" + e.getDuration());
                                request.answer(GuardianCalls.CALLBACK_ACTIVE_MUTE_ENTRY_RECEIVED.name(), list.toArray(String[]::new));
                            });
                        }
                    });
                    break;
                case REQUEST_GET_UNBAN_BEHAVIOR_ENTRIES:
                    this.proxyCore.getBanHandler().getBanBehaviorEntries(request.getData()[1], "CL", punishmentLogDocuments -> {
                        if (punishmentLogDocuments.size() == 0) {
                            request.answer(GuardianCalls.CALLBACK_ENTRIES_NOT_FOUND.name(), "null");
                        } else {
                            final LinkedList<String> list = new LinkedList<>();
                            for (PunishmentLogDocument e : punishmentLogDocuments) {
                                list.add(e.getId() + ">>" + e.getLogId() + ">>" + e.getTarget() + ">>" + e.getReason() + ">>" + e.getExecutor() + ">>" + e.getDate());
                            }
                            request.answer(GuardianCalls.CALLBACK_UNBAN_BEHAVIOR_ENTRIES_RECEIVED.name(), list.toArray(String[]::new));
                        }
                    });
                    break;
                case REQUEST_GET_UNMUTE_BEHAVIOR_ENTRIES:
                    this.proxyCore.getMuteHandler().getMuteBehaviorEntries(request.getData()[1], "CL", punishmentLogDocuments -> {
                        if (punishmentLogDocuments.size() == 0) {
                            request.answer(GuardianCalls.CALLBACK_ENTRIES_NOT_FOUND.name(), "null");
                        } else {
                            final LinkedList<String> list = new LinkedList<>();
                            for (PunishmentLogDocument e : punishmentLogDocuments) {
                                list.add(e.getId() + ">>" + e.getLogId() + ">>" + e.getTarget() + ">>" + e.getReason() + ">>" + e.getExecutor() + ">>" + e.getDate());
                            }
                            request.answer(GuardianCalls.CALLBACK_UNMUTE_BEHAVIOR_ENTRIES_RECEIVED.name(), list.toArray(String[]::new));
                        }
                    });
                    break;
            }
        }), "Guardian/Proxy", "guardian");
    }
}
