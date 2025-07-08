import { atom } from 'jotai'
import { PluginType } from '@/app/types/types'

export const pluginSelectAtom = atom<PluginType>(PluginType.REACT_NATIVE_AUDIO_RECORDER_PLAYER)